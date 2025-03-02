package com.sparta.backend.workspace.service;

import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import com.sparta.backend.workspace.dto.*;
import com.sparta.backend.workspace.entity.Invitation;
import com.sparta.backend.workspace.entity.Team;
import com.sparta.backend.workspace.entity.TeamUser;
import com.sparta.backend.workspace.exception.CustomException;
import com.sparta.backend.workspace.repository.InvitationRepository;
import com.sparta.backend.workspace.repository.TeamRepository;
import com.sparta.backend.workspace.repository.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final S3ImageService s3ImageService;

    @Value("${cloud.aws.s3.default-image-url}")     //기본이미지 url은 @Value 사용해서 설정된 환경 변수에서 가져옴(-)
    private String defaultImageUrl;

    //팀 생성 메서드
    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto, MultipartFile image) {  //RequestTeamDto 객체를 받고,ResponseTeamDto 객체를 반환함, requestTeamDto의 정보를 이용하여 team 객체를 생성하고, 생성된 team 객체를 teamRepository의 save 메서드를 사용해서 저장해서 saveteam 객체를 만든다. 그리고 ResponseTeamDto로 감싸서 반환한다.

        String imageUrl;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3ImageService.upload(image);
        } else {
            imageUrl = defaultImageUrl;
        }

        User creator = userRepository.findById(requestTeamDto.getCreatorId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User Not Found", "잘못된 팀 생성 입니다."));

        Team team = new Team(requestTeamDto); // 팀 객체 생성
        team.setImageUrl(imageUrl); //팀 엔티티에 이미지 url 설정
        Team saveTeam = teamRepository.save(team); // 팀 객체 데베에 저장
        TeamUser teamUser = new TeamUser(creator, saveTeam, "팀장"); //팀 생성자를 팀장으로 추가 //teamUser 객체 생성시 -> creator가 User 엔티티 객체, TeamUser 엔티티의 user 필드에 매핑됨,
        teamUserRepository.save(teamUser);
        // 변경 사항 저장 하는
        //userRepository.save(creator);
        ResponseTeamDto responseTeamDto = new ResponseTeamDto(saveTeam, creator.getUsername());
        responseTeamDto.setCreationDate(LocalDateTime.now());
        return responseTeamDto;
    }

    //controller 팀원 초대할 때 요청자가 팀장인지, 대상이 팀에 이미 속해 있는지, 초대장이 이미 있는지 확인

    //초대 생성 메서드
    //사용자가 팀에 새로운 멤버를 초대
    @Transactional
    public InvitationResponseDto inviteUserToTeam(InvitationRequestDto invitationRequestDto) {
        //요청받은 초대 요청 dto에서 팀id와 사용자id 추출
        Long teamId = invitationRequestDto.getTeamId(); //team의 fk 초대 할 팀의 아이디
        Long userId = invitationRequestDto.getUserId(); //user의 fk 초대 할 유저의 아이디
        Long requesterId = invitationRequestDto.getRequesterId();   //초대를 생성하는 유저의 아이디

        //요청자가 팀장인지 확인
        if (!isRequesterTeamLeader(requesterId, teamId)) { //userId -> requesterId 변경
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invitation failed", "요청자가 팀장이 아닙니다.");
        }
        //대상 사용자가 이미 팀에 속해 있는지 확인 -> 초대장을 보내서 초대하는 메서드 작성중이니깐 -> 이미 팀에 속해 있다면 예외를 던짐
        if(isUserInTeam(userId, teamId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invitation failed", "대상 사용자가 이미 팀에 속해 있습니다.");
        }
        //초대장이 이미 있는지 확인 -> 초대장이 이미 있다면 -> 이미 초대장이 있다고 예외를 던짐
        if(isInvitationExist(userId, teamId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invitation failed", "이미 초대장이 있습니다.");
        }

        //팀과 사용자를 조회 // -> 팀,유저,초대상태 정보를 dto객체로 변환 ->그 결과로 반환된 invitation 객체를 db에 저장-> 저장된 결과를 saveInvitation 변수에 할당함. -> saveInvitation를 responseDto로 감싸서 반환함.
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(invitationRequestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 이름 입니다."));
        User user = userRepository.findByIdAndIsDeleteFalse(invitationRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 이름 입니다."));

        Invitation invitation = new Invitation(team, user, requesterId);
        Invitation saveInvitation = invitationRepository.save(invitation);

        User leader = userRepository.findByIdAndIsDeleteFalse(team.getCreatorId()).orElse(null);

        InvitationResponseDto invitationResponseDto =
                new InvitationResponseDto(
                        saveInvitation.getId(),
                        saveInvitation.getUser().getId(),
                        team.getTeamId(),
                        team.getName(),
                        team.getDescription(),
                        invitation.getRequesterId(),
                        leader.getUsername(),
                        team.getImageUrl()
                );

        return invitationResponseDto;
    }

    //요청자가 팀장인지 확인하는 메서드 //다른 서비스나 컨트롤러에서 요청자가 팀장인지 확인하는데 사용될 수 있음
    public boolean isRequesterTeamLeader(Long requesterId, Long teamId) {
        //요청자 id로 user 객체를 조회

        User requester = userRepository.findByIdAndIsDeleteFalse(requesterId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User Not Found", "잘못된 사용자 ID 입니다."));
        //팀 ID로 Team 객체를 조회
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                .orElseThrow(() ->  new CustomException(HttpStatus.NOT_FOUND, "Team Not Found", "잘못된 팀 ID 입니다."));
        //User와 Team 객체를 사용하여 TeamUser를 조회 -> TeamUser 객체는 특정 팀에 속한 사용자의 역할을 정의
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, requester);  // //정의된 순서에 따라 호출 -> teamUserRepository에 정의 된 순서에 따라서 호출!
       // return teamUser != null && teamUser.getRole().equals("팀장");
        return teamUser != null && "팀장".equals(teamUser.getRole()); // ! 이 없으니깐

        //teamUser 객체가 존재하는지 확인 -> null이라면 해당 사용자가 해당 팀에 속하지 않는다는 의미
        //조회된 teamUser 객체의 역할이 "팀장"인지 확인 -> equals 사용해서, teamUser.getRole() 반환 값과 "팀장" 문자열을 비교
        // -> requesterId와 teamId를 사용하여 TeamUser 객체를 조회하는 방법 -> TeamUserRepository 에서 findByUserIdAndTeamId 메서드를 정의해 줘서!
    }

    //대상 사용자가 이미 팀에 속해 있는지 확인하는 메서드
    private boolean isUserInTeam(Long userId, Long teamId) {
        User user = userRepository.findByIdAndIsDeleteFalse(userId) //주어진 userId로 사용자를 조회한다. 존재하지 않는 사용자이면 예외 던짐.
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User Not Found", "잘못된 사용자 입니다."));
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Team Not Found", "잘못된 팀 입니다."));
        return teamUserRepository.existsByTeamAndUser(team, user);
    }

    //초대장이 이미 존재하는지 확인하는 메서드
    private boolean isInvitationExist(Long userId, Long teamId) {
        User user = userRepository.findByIdAndIsDeleteFalse(userId)
// userId에 해당하는 user 객체를 데이터베이스에서 조회한다 -> 만약 해당 userId에 해당하는 user 객체가 없으면 예외를 발생시킴 -> 조회 된 User 객체는 user 변수에 할당함.
                .orElseThrow(() ->  new CustomException(HttpStatus.NOT_FOUND, "User Not Found", "잘못된 사용자 입니다."));
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Team Not Found", "잘못된 팀 입니다."));
        return invitationRepository.existsByUserAndTeam(user, team);
    }

    //사용자가 초대받은 팀 목록 조회 메서드 (모든 상태)(+)
    public List<ResponseTeamInvitationIdDto> getAllTeamsByUserId(Long userId) {

        List<Invitation> invitations = invitationRepository.findByUserId(userId);
        // 초대장이 없는 경우 예외를 던짐
        if (invitations.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "초대장을 받은 적이 없습니다.");     //list로 반환하는 건 에러 발생 시  response 줄 필요 없음
        }
        return invitations.stream()
                .map(invitation -> {
                    Team team = invitation.getTeam();
                    User creator = userRepository.findById(team.getCreatorId())
                            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "팀 생성자를 찾을 수 없습니다."));  //(-)
                    // invitationId와 함께 responseTeamDto 생성
                    return new ResponseTeamInvitationIdDto(invitation.getId(), team, creator.getUsername());
                })
                .collect(Collectors.toList());
    }

    //초대 처리(수락,거부) 메서드(+) // 클라이언트가 요청한 초대
    @Transactional
    public InvitationSetResponseDto setInvitation(InvitationSetRequestDto invitationSetRequestDto){
        // 초대 ID로 초대 객체를 조회
        Invitation invitation = invitationRepository.findById(invitationSetRequestDto.getInvitationId())     //클라이언트가 보낸 초대 id가 실제로 존재하는지 확인해야됨.
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "잘못된 초대 ID 입니다."));

        if(invitationSetRequestDto.isAccept()){
            TeamUser teamUser = new TeamUser(invitation.getUser(), invitation.getTeam(),"팀원");
            //유저 추가 -> 초대장 삭제
            teamUserRepository.save(teamUser);
            invitation.setInvitationReceivedAt(LocalDateTime.now());  // 초대 수락 시간 설정
            invitationRepository.save(invitation);  // 초대 객체를 업데이트된 정보와 함께 저장
        }else{

        }
        //초대장 삭제
        invitationRepository.delete(invitation);
        return new InvitationSetResponseDto(invitation);
    }

    //팀원 삭제 메서드
    public String removeTeamMember(Long teamId, Long userId, Long requesterId) {
        //요청자가 팀장인지 확인
        if (!isRequesterTeamLeader(requesterId, teamId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Unauthorized", "팀장만 팀원을 삭제할 수 있습니다.");
        }
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "TeamId is incorrect", "잘못된 팀 ID 입니다."));
        User user = userRepository.findByIdAndIsDeleteFalse(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "UserId is incorrect", "잘못된 사용자 ID 입니다."));
        teamUserRepository.deleteByTeamAndUser(team, user); //** deleteByTeamAndUser이게 팀유저레포지토리에 있는 이유는,.(+) -> 해당 관계를 삭제하는 거기 때문에
        return "팀원이 삭제되었습니다.";
    }

    //팀 삭제 메서드
    public String removeTeam(Long teamId, Long requesterId) { //teamId -> 삭제할 팀을 고유하게 식별하는 값
        if (!isRequesterTeamLeader(requesterId, teamId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Unauthorized", "팀장만 팀원을 삭제할 수 있습니다.");
        }
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "TeamId is incorrect", "팀 삭제에 실패 했습니다."));
        //팀 삭제 전에 해당 팀과 관련된 모든 teamUser 관계 삭제
        teamUserRepository.deleteByTeam(team);
        //팀 삭제
        teamRepository.delete(team);
        return "팀이 삭제 되었습니다.";
    }
    //팀장 권한을 다른 팀원에게 물려주는 메서드
    public String transferTeamLeader(Long teamId, Long newLeaderId) {

        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "TeamId is incorrect", "잘못된 팀 ID 입니다."));

        User newLeader = userRepository.findByIdAndIsDeleteFalse(newLeaderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "UserId is incorrect", "잘못된 사용자 ID 입니다."));
        //기존 팀장을 찾아서 역할을 변경
        TeamUser currentLeader = teamUserRepository.findByTeamAndRole(team, "팀장") //현재 팀장 조회 -> 특정 팀의 역할이 "팀장"인 TeamUser 객체 반환
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Current team leader not found","현재 팀장을 찾을 수 없습니다."));     //(-)
        currentLeader.setRole("팀원");    //역할 변경 / 조회된 TeamUser 객체의 role 필드를 팀장 -> 팀원으로 변경
        teamUserRepository.save(currentLeader); //변경된 TeamUser 객체를 db에 저장 // 변경된 역할 정보가 반영
        //새로운 팀장을 설정
        TeamUser newTeamLeader = teamUserRepository.findByTeamAndUser(team, newLeader);
        if (newTeamLeader == null) {
            //새로운 리더가 팀에 속해있지 않다면 추가
            newTeamLeader = new TeamUser(newLeader, team, "팀장");
        } else {
            //기존 멤버라면 역할 변경만
            newTeamLeader.setRole("팀장");
        }
        teamUserRepository.save(newTeamLeader); //변경된 역할 정보 저장
        return "팀장 권한을 이전했습니다.";
    }
    //팀 수정
        @Transactional //성공적으로 완료  or 하나라도 실패 시 전체 작업 롤백 -> 수정 도중 예외 발생 시 변경 하기전으로 복원 필요
        public TeamUpdateResponseDto updateTeam(Long teamId, TeamUpdateRequestDto teamUpdateRequestDto) {
            Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId) //팀 id로 팀을 조회, 존재하지 않으면 예외를 던짐.
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "TeamId is incorrect", "잘못된 팀 ID 입니다."));
            //요청 dto에서 팀 이름이 null이 아닌경우 팀 이름을 업데이트 한다.
            if (teamUpdateRequestDto.getName() != null) {
                team.setName(teamUpdateRequestDto.getName());
            }
            if(teamUpdateRequestDto.getDescription() != null) {
                team.setDescription(teamUpdateRequestDto.getDescription());
            }
            if (teamUpdateRequestDto.getImageUrl() != null) {
                team.setImageUrl(teamUpdateRequestDto.getImageUrl());
            }
            Team updateTeam = teamRepository.save(team);
            return new TeamUpdateResponseDto(updateTeam.getTeamId(), updateTeam.getName(), updateTeam.getDescription(), updateTeam.getImageUrl()); //응답 dto를 반환하여 클라이언트에게 업데이트 된 팀 정보 전달
    }

    //하나의 팀에 소속된 전체 유저 목록 조회 메서드(+)
    public List<UsersInTeamResponseDto> getUsersInTeam(Long teamId) {
        Team team = teamRepository.findByTeamIdAndIsDeleteFalse(teamId)
                       .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "잘못된 팀 ID 입니다."));

        //List<TeamUser> teamUsers = teamUserRepository.findByTeam(team);
        return teamUserRepository.findByTeam(team).stream()
                .map(teamUser -> new UsersInTeamResponseDto(
                        team.getTeamId(),  //팀 id
                        teamUser.getUser().getId(), //유저 id
                        teamUser.getUser().getUsername(),  //유저 이름
                        teamUser.getUser().getEmail(),
                        teamUser.getJoinedAt() //팀에 합류한 시간
                        ))
                .collect(Collectors.toList());  //스트림 결과를 리스트로 변환하여 반환
    }

      //유저가 속한 팀 전체 목록 조회 메서드
        public List<CustomResponseTeamDto> getTeamsByUserId(Long userId) {
            //찾고자 하는 사용자가 속한 TeamUser 리스트
            List<TeamUser> teamUsers = teamUserRepository.findAllByUserId(userId);

            // 팀이 없을 경우 예외 던지기
            if (teamUsers.isEmpty()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "Bad Request", "속해있는 팀이 없습니다.");
            }
            //return을 위한 빈 리스트 생성
            List<CustomResponseTeamDto> teamDtos = new ArrayList<>();
            //사용자가 속한 TeamUser 리스트 갯수만큼 loop
            for(int i = 0; i < teamUsers.size(); i++){
                TeamUser teamUser = teamUsers.get(i);
                Team team = teamUser.getTeam();
                User user = teamUser.getUser();
                String username = user.getUsername();
                //삭제되지 않은 팀이면
                if (team.getIsDelete() == false) {
                    //response객체 list에 add
                    CustomResponseTeamDto customResponseTeamDto = new CustomResponseTeamDto(team, username);
                    teamDtos.add(customResponseTeamDto);
                }
            }
            return teamDtos;
    }
}

