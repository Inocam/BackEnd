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
}
