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

    //팀 생성 엔드포인트
    //createteam 메서드는 ResponseEntity<ResponseTeamDto> 타입의 응답을 반환
    //메서드 인자는 @RequestBody 사용해서 요청 본문을 RequestTeamDto 객체로 매핑함
    //클라이언트가 전송한 json 데이터가 RequestTeamDto 객체로 변환됨을 의미
    //reqyestTeamDto : 클라이언트가 전송한 팀 생성 요청 데이터가 담긴 객체, 팀 생성에 필요한 정보를 포함함
    //teamService 의 createTeam 메서드를 호출하여 팀을 생성
    //responseTeamDto 를 createTeam 메서드에 전달하여 서비스 계층에서 팀 생성로직을 처리
    //서비스 계층은 비즈니스 로직을 담당하며, DB와의 상호작용, 데이터 검증, 예외 처리 등을 수행
    //createteam 메서드는 팀 생성 후 responseTeamDto 객체를 반환함, 이 객체(생성된 팀id, 이름 등)는 클라이언트에게 반환할 정보가 포함됨 -> 민감한 정보는 넣지 말아야함
    //ResponseEntity 객체를 생성하여 http 응답을 구성함
    //응답 본문으로 responseteamDto를 설정하고, http 상태 코드를 설정해줌 (201, 성공적으로 생성되었음을 알리는 http)

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

//    // 초대 수락 엔드포인트 // 수락,거부 따로 만들필요없이
//    @PostMapping("/invite/accept/{invitationId}")
//    public ResponseEntity<ResponseInvitationDto> acceptInvitation(@PathVariable Long invitationId) {
//        ResponseInvitationDto responseInvitationDto = teamService.acceptInvitation(invitationId);
//        return ResponseEntity.ok(responseInvitationDto);
//    }
//
//    // 초대 거부 엔드포인트
//    @PostMapping("/invite/reject/{invitationId}")
//    public ResponseEntity<ResponseInvitationDto> rejectInvitation(@PathVariable Long invitationId) {
//        ResponseInvitationDto responseInvitationDto = teamService.rejectInvitation(invitationId);
//        return ResponseEntity.ok(responseInvitationDto);
//    }

    //팀원 삭제 엔드포인트
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<Void> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeTeamMember(teamId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); //응답 본문 없이 204 상태 코드 반환
    }
}
