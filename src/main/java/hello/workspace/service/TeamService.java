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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    //팀 생성 메서드
    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto) {  //RequestTeamDto 객체를 받고,ResponseTeamDto 객체를 반환함, requestTeamDto의 정보를 이용하여 team 객체를 생성하고, 생성된 team 객체를 teamRepository의 save 메서드를 사용해서 저장해서 saveteam 객체를 만든다. 그리고 ResponseTeamDto로 감싸서 반환한다.
        Team team = new Team(requestTeamDto);
        Team saveTeam = teamRepository.save(team);
        return new ResponseTeamDto(saveTeam);
    }

//    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto) {  //RequestTeamDto 객체를 받고,ResponseTeamDto 객체를 반환함, requestTeamDto의 정보를 이용하여 team 객체를 생성하고, 생성된 team 객체를 teamRepository의 save 메서드를 사용해서 저장해서 saveteam 객체를 만든다. 그리고 ResponseTeamDto로 감싸서 반환한다.
//        logger.debug("Creating team with details: {}", requestTeamDto);
//        Team team = new Team(requestTeamDto);
//        Team saveTeam = teamRepository.save(team);
//        logger.debug("Saved team: {}", saveTeam);
//        return new ResponseTeamDto(saveTeam);
//    }

    //초대 생성 메서드
    public ResponseInvitationDto inviteUserToTeam(RequestInvitationDto requestInvitationDto) {
        Team team = teamRepository.findById(requestInvitationDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 이름 입니다."));
        User user = userRepository.findById(requestInvitationDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 이름 입니다."));

        Invitation invitation = new Invitation(requestInvitationDto.getStatus(), team, user);
        Invitation saveInvitation = invitationRepository.save(invitation);

        return new ResponseInvitationDto(saveInvitation);
    }

    //왜 list로 작성하는지  -> 사용자 ID를 기반으로 사용자가 초대받은 팀의 목록을 반환하기 위해
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

    public String setInvitation(InvitationSetRequestDTO invitationSetRequestDTO){
        Invitation invitation = invitationRepository.findById(invitationSetRequestDTO.getInvitationId())
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
        invitationRepository.delete(invitation);
        return responseString;
    }
    //팀원 삭제 메서드
    public void removeTeamMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 팀 ID입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));
        teamUserRepository.deleteByTeamAndUser(team, user);
    }
}

