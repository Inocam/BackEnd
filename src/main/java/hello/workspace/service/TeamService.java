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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    //팀 생성 메서드
    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto) {  //RequestTeamDto 객체를 받고,ResponseTeamDto 객체를 반환함, requestTeamDto의 정보를 이용하여 team 객체를 생성하고, 생성된 team 객체를 teamRepository의 save 메서드를 사용해서 저장해서 saveteam 객체를 만든다. 그리고 ResponseTeamDto로 감싸서 반환한다.
        Team team = new Team(requestTeamDto); // 팀 객체 생성
        User creator = userRepository.findById(requestTeamDto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 iD입니다"));

        Team saveTeam = teamRepository.save(team); // 팀 객체 데베에 저장

        TeamUser teamUser = new TeamUser(creator, saveTeam, "팀장"); //팀 생성자를 팀장으로 추가
        teamUserRepository.save(teamUser);

        return new ResponseTeamDto(saveTeam);
    }

    //controller 팀원 초대할 때 요청자가 팀장인지, 대상이 팀에 이미 속해 있는지, 초대장이 이미 있는지 확인

    //초대 생성 메서드
    //사용자가 팀에 새로운 멤버를 초대
    public InvitationResponseDto inviteUserToTeam(InvitationRequestDto invitationRequestDto) {
        //요청받은 초대 요청 dto에서 팀id와 사용자id 추출
        Long teamId = invitationRequestDto.getTeamId();
        Long userId = invitationRequestDto.getUserId();
        Long requestId = invitationRequestDto.getRequesterId(); //요청자를 invitationRequestDto 에서 가져온다고 가정.. 지금 저기에 작성 안되어 있음

        //요청자가 팀장인지 확인
        if (isRequesterTeamLeader(userId, teamId)) {
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

        Invitation invitation = new Invitation(invitationRequestDto.getStatus(), team, user);
        Invitation saveInvitation = invitationRepository.save(invitation);

        return new InvitationResponseDto(saveInvitation);
    }

    //요청자가 팀장인지 확인하는 메서드 //다른 서비스나 컨트롤러에서 요청자가 팀장인지 확인하는데 사용될 수 있음
    public boolean isRequesterTeamLeader(Long requesterId, Long teamId) {
        //요청자 id로 user 객체를 조회
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID 입니다."));
        //팀 ID로 Team 객체를 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 ID입니다."));
        //User와 Team 객체를 사용하여 TeamUser를 조회 -> TeamUser 객체는 특정 팀에 속한 사용자의 역할을 정의
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, requester);  // //정의된 순서에 따라 호출 -> teamUserRepository에 정의 된 순서에 따라서 호출!
        return teamUser != null && teamUser.getRole().equals("팀장");

        //teamUser 객체가 존재하는지 확인 -> null이라면 해당 사용자가 해당 팀에 속하지 않는다는 의미
        //조회된 teamUser 객체의 역할이 "팀장"인지 확인 -> equals 사용해서, teamUser.getRole() 반환 값과 "팀장" 문자열을 비교
        // -> requesterId와 teamId를 사용하여 TeamUser 객체를 조회하는 방법 -> TeamUserRepository 에서 findByUserIdAndTeamId 메서드를 정의해 줘서!
    }

    //대상 사용자가 이미 팀에 속해 있는지 확인하는 메서드
    private boolean isUserInTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId)
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
        //
    }

    //사용자가 초대받은 팀 목록 조회 메서드
    public List<ResponseTeamDto> getTeamsByUserId(Long userId) {
        //'accepted' 상태의 초대만 조회
        List<Invitation> invitations = invitationRepository.findByUserIdAndStatus(userId, "accepted");
        return invitations.stream()
                .map(invitation -> new ResponseTeamDto(invitation.getTeam()))
                .collect(Collectors.toList());
    }

//    //초대 수락 메서드
//    public ResponseInvitationDto acceptInvitation(Long invitationId) {
//        Invitation invitation = invitationRepository.findById(invitationId)
//                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대 ID 입니다."));
//        invitation.setStatus("accepted");
//        Invitation updqtedInvitation = invitationRepository.save(invitation);
//        ResponseInvitationDto responseInvitationDto = new ResponseInvitationDto(updqtedInvitation);
//        responseInvitationDto.setMessage("초대가 수락되었습니다.");
//        return responseInvitationDto;
//    } // -> 수락만 하고 팀원에 속하는 메서드가 없음(-)
//
//
//    // 초대 거부 메서드
//    public ResponseInvitationDto rejectInvitation(Long invitationId) {
//        Invitation invitation = invitationRepository.findById(invitationId)     //초대ID로 초대를 조회,존재하지 않으면 예외를 던짐
//                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대 ID입니다."));
//        invitation.setStatus("rejected");   //초대 상태를 rejected로 변경
//        Invitation updatedInvitation = invitationRepository.save(invitation);   //변경된 초대를 db에 저장
//        ResponseInvitationDto responseInvitationDto = new ResponseInvitationDto(updatedInvitation);
//        responseInvitationDto.setMessage("초대가 거부되었습니다.");
//        return responseInvitationDto;  //업데이트된 초대 정보를 dto로 변환하여 반환
//    }


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
        teamUserRepository.deleteByTeamAndUser(team, user); //** deleteByTeamAndUser이게 팀유저레포지토리에 있는 이유는,.(-)
    }
}

