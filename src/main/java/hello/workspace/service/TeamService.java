package hello.workspace.service;

import hello.workspace.dto.RequestInvitationDto;
import hello.workspace.dto.RequestTeamDto;
import hello.workspace.dto.ResponseInvitationDto;
import hello.workspace.dto.ResponseTeamDto;
import hello.workspace.entity.Invitation;
import hello.workspace.entity.Team;
import hello.workspace.entity.User;
import hello.workspace.repository.InvitationRepository;
import hello.workspace.repository.TeamRepository;
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

    //팀 생성 메서드
    public ResponseTeamDto createTeam(RequestTeamDto requestTeamDto) {
        Team team = new Team(requestTeamDto);
        Team saveTeam = teamRepository.save(team);
        return new ResponseTeamDto(saveTeam);
    }

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


}