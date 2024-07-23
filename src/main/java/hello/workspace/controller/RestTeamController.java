package hello.workspace.controller;

import hello.workspace.dto.RequestInvitationDto;
import hello.workspace.dto.RequestTeamDto;
import hello.workspace.dto.ResponseInvitationDto;
import hello.workspace.dto.ResponseTeamDto;
import hello.workspace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class RestTeamController {
    private final TeamService teamService;

    //팀 생성 엔드포인트
    @PostMapping
    public ResponseEntity<ResponseTeamDto> createTeam(@RequestBody RequestTeamDto requestTeamDto) {
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto);
        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
    }
    //팀원 초대 엔드포인트
    @PostMapping("/invite")
    public ResponseEntity<ResponseInvitationDto> inviteTeam(@RequestBody RequestInvitationDto requestInvitationDto) {
        ResponseInvitationDto responseInvitationDto = teamService.inviteUserToTeam(requestInvitationDto);
        return new ResponseEntity<>(responseInvitationDto, HttpStatus.CREATED);

    }

    //사용자가 초대받은 팀 목록 조회 엔드포인트
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResponseTeamDto>> getTeamsByUserId(@PathVariable Long userId) {
        List<ResponseTeamDto> teams = teamService.getTeamsByUserId(userId);
        return ResponseEntity.ok(teams);
    }

    // 초대 수락 엔드포인트
    @PostMapping("/invite/accept/{invitationId}")
    public ResponseEntity<ResponseInvitationDto> acceptInvitation(@PathVariable Long invitationId) {
        ResponseInvitationDto responseInvitationDto = teamService.acceptInvitation(invitationId);
        return ResponseEntity.ok(responseInvitationDto);
    }

    // 초대 거부 엔드포인트
    @PostMapping("/invite/reject/{invitationId}")
    public ResponseEntity<ResponseInvitationDto> rejectInvitation(@PathVariable Long invitationId) {
        ResponseInvitationDto responseInvitationDto = teamService.rejectInvitation(invitationId);
        return ResponseEntity.ok(responseInvitationDto);
    }

    //팀원 삭제 엔드포인트
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeTeamMember(teamId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //응답 본문 없이 204 상태 코드 반환
    }
}
