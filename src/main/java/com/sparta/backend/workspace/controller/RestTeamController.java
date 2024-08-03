package com.sparta.backend.workspace.controller;

import com.sparta.backend.workspace.dto.*;
import com.sparta.backend.workspace.exception.ErrorResponse;
import com.sparta.backend.workspace.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foot/teams")
@RequiredArgsConstructor
@Slf4j
public class RestTeamController {


    private final TeamService teamService;


    /* ______________________________Team___________________________________________________ */
    //팀 생성 엔드포인트(+) 팀장 추가(+)
    //creatorId 추가함 -> 팀 생성 요청 시 팀의 생성자를 추가하기 위해서, 누가 팀을 생성했는지를 설정!
    @PostMapping
    public ResponseEntity<ResponseTeamDto> createTeam(@RequestBody RequestTeamDto requestTeamDto) {
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto);
        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
    }


    /* ______________________________Invitation___________________________________________________ */

    //controller 팀원 초대할 때 요청자가 팀장인지, 대상이 팀에 이미 속해 있는지, 초대장이 이미 있는지 확인
    //팀원 초대 엔드포인트(+)
    @PostMapping("/invite")
    public ResponseEntity<InvitationResponseDto> inviteTeam(@RequestBody InvitationRequestDto invitationRequestDto) {
        InvitationResponseDto responseInvitationDto = teamService.inviteUserToTeam(invitationRequestDto);
        return new ResponseEntity<>(responseInvitationDto, HttpStatus.CREATED);
    }

    // 초대 처리 엔드포인트
    @DeleteMapping("/invite")
    public ResponseEntity<String> setInvitation(@RequestBody InvitationSetRequestDto invitationSetRequestDto) {
        log.info("Controller received: invitationId={}, isAccept={}",
                invitationSetRequestDto.getInvitationId(), invitationSetRequestDto.isAccept());
        String responseString = teamService.setInvitation(invitationSetRequestDto);
        return ResponseEntity.ok(responseString);
    }

    //사용자가 초대받은 모든 팀 목록 조회 엔드포인트
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<ResponseTeamDto>> getAllTeamsByUserId(@PathVariable Long userId) {
        List<ResponseTeamDto> teams = teamService.getAllTeamsByUserId(userId);
        return ResponseEntity.ok(teams);
    }


    //팀원 삭제 엔드포인트 // -> temaid와 userid를 사용하여 해당 팀과 사용자 간의 관계를 삭제하는 방식 -> 팀에서 특정 사용자 삭제
    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<ErrorResponse> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        String message = teamService.removeTeamMember(teamId, userId);
        ErrorResponse errorResponse = new ErrorResponse("SUCCESS", message, HttpStatus.OK.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);  //응답 본문 없이 204 상태 코드 반환
    }

    //팀 삭제 엔드포인트(+)
    @DeleteMapping("/{teamId}")
    public ResponseEntity<ErrorResponse> removeTeam(@PathVariable Long teamId) {
       String message = teamService.removeTeam(teamId);
        ErrorResponse errorResponse = new ErrorResponse("SUCCESS", message, HttpStatus.OK.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    //팀장 권한 주기
    @PostMapping("/{teamId}/transfer-leader/{newLeaderId}")
    public ResponseEntity<ErrorResponse> transferTeamLeader(@PathVariable Long teamId, @PathVariable Long newLeaderId) {
        String message = teamService.transferTeamLeader(teamId, newLeaderId);
        ErrorResponse errorResponse = new ErrorResponse("SUCCESS", message, HttpStatus.OK.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    //팀 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<TeamUpdateResponseDto> updateTeam(@PathVariable Long teamId, @RequestBody TeamUpdateRequestDto teamUpdateRequestDto) {
        TeamUpdateResponseDto updatedTeam = teamService.updateTeam(teamId, teamUpdateRequestDto);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    //하나의 팀에 소속된 전체 유저 목록 조회
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UsersInTeamResponseDto>> getUsersInTeam(@PathVariable Long teamId) {
        List<UsersInTeamResponseDto> members = teamService.getUsersInTeam(teamId);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}



