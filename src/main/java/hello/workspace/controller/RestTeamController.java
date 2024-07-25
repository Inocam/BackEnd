package hello.workspace.controller;

import hello.workspace.dto.*;
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


    /* ______________________________Team___________________________________________________ */
    //팀 생성 엔드포인트
    //creatorId 추가함 -> 팀 생성 요청 시 팀의 생성자를 추가하기 위해서, 누가 팀을 생성했는지를 설정!
    @PostMapping
    public ResponseEntity<ResponseTeamDto> createTeam(@RequestBody RequestTeamDto requestTeamDto) {
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto);
        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
    }


    /* ______________________________Invitation___________________________________________________ */

    //controller 팀원 초대할 때 요청자가 팀장인지, 대상이 팀에 이미 속해 있는지, 초대장이 이미 있는지 확인
    //팀원 초대 엔드포인트
    @PostMapping("/invite")
    public ResponseEntity<InvitationResponseDto> inviteTeam(@RequestBody InvitationRequestDto invitationRequestDto) {
        InvitationResponseDto responseInvitationDto = teamService.inviteUserToTeam(invitationRequestDto);
        return new ResponseEntity<>(responseInvitationDto, HttpStatus.CREATED);
    }

    // 초대 처리 엔드포인트
    @DeleteMapping("/invite")
    public ResponseEntity<String> setInvitation(@RequestBody InvitationSetRequestDTO invitationSetRequestDTO) {
        String responseString = teamService.setInvitation(invitationSetRequestDTO);
        return ResponseEntity.ok(responseString);
    }

    //사용자가 초대받은 팀 목록 조회 엔드포인트
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResponseTeamDto>> getTeamsByUserId(@PathVariable Long userId) {
        List<ResponseTeamDto> teams = teamService.getTeamsByUserId(userId);
        return ResponseEntity.ok(teams);
    }

    //팀원 삭제 엔드포인트
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeTeamMember(teamId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //응답 본문 없이 204 상태 코드 반환
    }
}
