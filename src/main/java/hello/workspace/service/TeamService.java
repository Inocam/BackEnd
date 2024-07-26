package hello.workspace.service;

import hello.workspace.dto.*;
import hello.workspace.entity.Invitation;
import hello.workspace.entity.Team;
import hello.workspace.entity.TeamUser;
import hello.workspace.entity.User;
import hello.workspace.repository.InvitationRepository;
import hello.workspace.repository.TeamRepository;
import hello.workspace.repository.TeamUserRepository;
import hello.workspace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    //팀 생성 메서드
    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto) {  //RequestTeamDto 객체를 받고,ResponseTeamDto 객체를 반환함, requestTeamDto의 정보를 이용하여 team 객체를 생성하고, 생성된 team 객체를 teamRepository의 save 메서드를 사용해서 저장해서 saveteam 객체를 만든다. 그리고 ResponseTeamDto로 감싸서 반환한다.
        Team team = new Team(requestTeamDto); // 팀 객체 생성
        User creator = userRepository.findById(requestTeamDto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 생성 입니다."));

        Team saveTeam = teamRepository.save(team); // 팀 객체 데베에 저장

        TeamUser teamUser = new TeamUser(creator, saveTeam, "팀장"); //팀 생성자를 팀장으로 추가 //teamUser 객체 생성시 -> creator가 User 엔티티 객체, TeamUser 엔티티의 user 필드에 매핑됨,
        teamUserRepository.save(teamUser);
        // 변경 사항 저장 하는
        //userRepository.save(creator);
        return new ResponseTeamDto(saveTeam);
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
        if (isRequesterTeamLeader(requesterId, teamId)) { //userId -> requesterId 변경
            throw new IllegalArgumentException("요청자가 팀장이 아닙니다.");
        }
        //대상 사용자가 이미 팀에 속해 있는지 확인 -> 초대장을 보내서 초대하는 메서드 작성중이니깐 -> 이미 팀에 속해 있다면 예외를 던짐
        if(isUserInTeam(userId, teamId)) {
            throw new IllegalArgumentException("대상 사용자가 이미 팀에 속해 있습니다.");
        }
        //초대장이 이미 있는지 확인 -> 초대장이 이미 있다면 -> 이미 초대장이 있다고 예외를 던짐
        if(isInvitationExist(userId, teamId)) {
            throw new IllegalArgumentException("이미 초대장이 있습니다.");
        }

        //팀과 사용자를 조회 // -> 팀,유저,초대상태 정보를 dto객체로 변환 ->그 결과로 반환된 invitation 객체를 db에 저장-> 저장된 결과를 saveInvitation 변수에 할당함. -> saveInvitation를 responseDto로 감싸서 반환함.
        Team team = teamRepository.findById(invitationRequestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 이름 입니다."));
        User user = userRepository.findById(invitationRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 이름 입니다."));

        Invitation invitation = new Invitation(invitationRequestDto.getStatus(), team, user, requesterId);
        Invitation saveInvitation = invitationRepository.save(invitation);

        return new InvitationResponseDto(saveInvitation);
    }

    //요청자가 팀장인지 확인하는 메서드 //다른 서비스나 컨트롤러에서 요청자가 팀장인지 확인하는데 사용될 수 있음
    public boolean isRequesterTeamLeader(Long requesterId, Long teamId) {
        //요청자 id로 user 객체를 조회
        log.info("요청자 ID: {}", requesterId);
        log.info("팀 ID: {}", teamId);

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID 입니다."));
        log.info("요청자: {}", requester.getUsername());

        //팀 ID로 Team 객체를 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 ID입니다."));
        log.info("팀: {}", team.getName());

        //User와 Team 객체를 사용하여 TeamUser를 조회 -> TeamUser 객체는 특정 팀에 속한 사용자의 역할을 정의
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, requester);  // //정의된 순서에 따라 호출 -> teamUserRepository에 정의 된 순서에 따라서 호출!
       // return teamUser != null && teamUser.getRole().equals("팀장");

        if (teamUser != null) {
            log.info("요청자 역할: {}", teamUser.getRole());
        } else {
            log.info("요청자는 팀의 일부가 아닙니다.");
        }

        log.info("팀: {}", teamUser.getRole());
        log.info("팀2: {}", "팀장".equals(teamUser.getRole()));
        return !(teamUser != null && "팀장".equals(teamUser.getRole()));  // ! 이 없으니깐

        //teamUser 객체가 존재하는지 확인 -> null이라면 해당 사용자가 해당 팀에 속하지 않는다는 의미
        //조회된 teamUser 객체의 역할이 "팀장"인지 확인 -> equals 사용해서, teamUser.getRole() 반환 값과 "팀장" 문자열을 비교
        // -> requesterId와 teamId를 사용하여 TeamUser 객체를 조회하는 방법 -> TeamUserRepository 에서 findByUserIdAndTeamId 메서드를 정의해 줘서!
    }

    //대상 사용자가 이미 팀에 속해 있는지 확인하는 메서드
    private boolean isUserInTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId) //주어진 userId로 사용자를 조회한다. 존재하지 않는 사용자이면 예외 던짐.
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 입니다."));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 입니다."));
        return teamUserRepository.existsByTeamAndUser(team, user);
    }

    //초대장이 이미 존재하는지 확인하는 메서드 // (userId, teamId) 로 하는게 맞는건지..?
    private boolean isInvitationExist(Long userId, Long teamId) {
        User user = userRepository.findById(userId)
// userId에 해당하는 user 객체를 데이터베이스에서 조회한다 -> 만약 해당 userId에 해당하는 user 객체가 없으면 예외를 발생시킴 -> 조회 된 User 객체는 user 변수에 할당함.
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 입니다."));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 입니다."));
        return invitationRepository.existsByUserAndTeam(user, team);
    }

    //사용자가 초대받은 팀 목록 조회 메서드 (모든 상태)(+)
    public List<ResponseTeamDto> getAllTeamsByUserId(Long userId) {
        List<Invitation> invitations = invitationRepository.findByUserId(userId);
        return invitations.stream()
                .map(invitation -> new ResponseTeamDto(invitation.getTeam()))
                .collect(Collectors.toList());
    }

    //사용자가 초대받은 팀 목록 조회 메서드(+) (뺄지,, 말지??(-))
    public List<ResponseTeamDto> getTeamsByUserId(Long userId) {
        //'accepted' 상태의 초대만 조회
        List<Invitation> invitations = invitationRepository.findByUserIdAndStatus(userId, "accepted");
        return invitations.stream()
                .map(invitation -> new ResponseTeamDto(invitation.getTeam()))
                .collect(Collectors.toList());
    }

    //초대 수락,거부 메서드 // 클라이언트가 요청한 초대
    public String setInvitation(InvitationSetRequestDTO invitationSetRequestDTO){
        // 초대 ID로 초대 객체를 조회
        Invitation invitation = invitationRepository.findById(invitationSetRequestDTO.getInvitationId())     //클라이언트가 보낸 초대 id가 실제로 존재하는지 확인해야됨.
                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대 ID입니다."));
        String responseString;  //변수 생명주기 때문에 여기 넣어줌.
        if(invitationSetRequestDTO.isAccept()){
            TeamUser teamUser = new TeamUser(invitation.getUser(), invitation.getTeam(),"팀원");
            //유저 추가 -> 초대장 삭제
            teamUserRepository.save(teamUser);
            responseString = "초대가 완료되었습니다.";
        }else{
            responseString = "초대가 거부되었습니다.";
        }
        //초대장 삭제
        invitationRepository.delete(invitation);
        return responseString;
    }

    //팀원 삭제 메서드
    public void removeTeamMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 ID입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));
        teamUserRepository.deleteByTeamAndUser(team, user); //** deleteByTeamAndUser이게 팀유저레포지토리에 있는 이유는,.(+) -> 해당 관계를 삭제하는 거기 때문에
    }

    //팀 삭제 메서드
    public void removeTeam(Long teamId) { //teamId -> 삭제할 팀을 고유하게 식별하는 값
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 Id 입니다."));
        //팀 삭제 전에 해당 팀과 관련된 모든 teamUser 관계 삭제
        teamUserRepository.deleteByTeam(team);
        //팀 삭제
        teamRepository.delete(team);
    }
}

