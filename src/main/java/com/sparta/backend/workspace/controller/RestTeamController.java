package com.sparta.backend.workspace.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.backend.workspace.dto.*;
import com.sparta.backend.workspace.exception.ErrorResponse;
import com.sparta.backend.workspace.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/foot/teams")
@RequiredArgsConstructor
@Slf4j
public class RestTeamController {

    private final TeamService teamService;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    /* ______________________________Team___________________________________________________ */

    //팀 생성 엔드포인트(+) 팀장 추가(+)
    //creatorId 추가함 -> 팀 생성 요청 시 팀의 생성자를 추가하기 위해서, 누가 팀을 생성했는지를 설정!

//    @PostMapping
//    public ResponseEntity<ResponseTeamDto> createTeam(@RequestBody RequestTeamDto requestTeamDto) {
//        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto);
//        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
//    }
    //클라이언트가  JSON 데이터를 포함하는 하나의 multipart/form-data 요청을 보내야함 -> json 데이터를 파일과 함께 전송해야하기 때문에 코드가 복잡해짐

    //팀 스페이스 생성 (json 데이터와 파일을 함께 보내는 방법)
//    @PostMapping
//    public ResponseEntity<ResponseTeamDto> createTeam(@RequestPart("team") RequestTeamDto requestTeamDto,
//                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
//        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto, image);
//        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
//    }
    //이 방식은 파일이 없는 경우에도 multipart/form-data 형식을 사용해야함 -> 파일 처리와 json 데이터 처리가 혼합 -> 복잡


    //팀 스페이스 생성 (json 데이터)
    @PostMapping
    public ResponseEntity<ResponseTeamDto> createTeam(@RequestBody RequestTeamDto requestTeamDto) {
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto, null);
        return new ResponseEntity<>(responseTeamDto, HttpStatus.CREATED);
    }

    /* ______________________________Invitation___________________________________________________ */

    //controller 팀원 초대할 때 요청자가 팀장인지, 대상이 팀에 이미 속해 있는지, 초대장이 이미 있는지 확인
    //팀원 초대 엔드포인트(+)
    @PostMapping("/invitations")    //동작은 어노테이션 매핑으로 나타내고, 명사로 작성하는 게 좋음***
    public ResponseEntity<InvitationResponseDto> inviteTeam(@RequestBody InvitationRequestDto invitationRequestDto) {
        InvitationResponseDto responseInvitationDto = teamService.inviteUserToTeam(invitationRequestDto);
        return new ResponseEntity<>(responseInvitationDto, HttpStatus.CREATED);
    }

    // 초대 처리 엔드포인트
    @PostMapping("/invitations/{invitationId}")
    public ResponseEntity<InvitationSetResponseDto> setInvitation(@RequestBody InvitationSetRequestDto invitationSetRequestDto) {
        log.info("Controller received: invitationId={}, isAccept={}",
                invitationSetRequestDto.getInvitationId(), invitationSetRequestDto.isAccept());
        // Service Layer 호출
        InvitationSetResponseDto invitationSetResponseDto = teamService.setInvitation(invitationSetRequestDto);
        return ResponseEntity.ok(invitationSetResponseDto);
    }

    //초대받은 모든 팀 목록 조회 엔드포인트
    @GetMapping("/user/{userId}/invitations")
    public ResponseEntity<List<ResponseTeamInvitationIdDto>> getAllTeamsByUserId(@PathVariable Long userId) {
        List<ResponseTeamInvitationIdDto> teams = teamService.getAllTeamsByUserId(userId);
        return ResponseEntity.ok(teams);
    }

    //팀원 삭제 엔드포인트 (+)// -> temaid와 userid를 사용하여 해당 팀과 사용자 간의 관계를 삭제하는 방식 -> 팀에서 특정 사용자 삭제
    @DeleteMapping("/{teamId}/members/{userId}")    //requesterId는 요청 본문에 포함
    public ResponseEntity<ErrorResponse> removeTeamMember(@PathVariable Long teamId, @PathVariable Long userId, @PathVariable Long requesterId) {   //(-) pathvariable 3개 받으면 dto가 더 깔끔?? > 어떤 경우에 pathvarible 을 쓰고, dto를 쓰는지..***
        log.info("removeTeamMember called with teamId={}, userId={}, requesterId={}", teamId, userId, requesterId);

        String message = teamService.removeTeamMember(teamId, userId, requesterId);
        ErrorResponse errorResponse = new ErrorResponse("SUCCESS", message, HttpStatus.OK.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);  //응답 본문 없이 204 상태 코드 반환
    }

    //팀 삭제 엔드포인트(+)
    @DeleteMapping("/{teamId}") //requesterId는 요청 본문에 포함
    public ResponseEntity<ErrorResponse> removeTeam(@PathVariable Long teamId, @PathVariable Long requesterId) {
       String message = teamService.removeTeam(teamId, requesterId);
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
    @PutMapping("/{teamId}") //json  데이터와, 멀티파트 데이터를 모두 받아들일 것임을 명시해줌
    public ResponseEntity<TeamUpdateResponseDto> updateTeam(
            @PathVariable Long teamId,
            @ModelAttribute(value = "data") TeamUpdateRequestDto teamUpdateRequestDto,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        if(image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename(); //고유한 파일 이름을 생성
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), new ObjectMetadata()));
                String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
                teamUpdateRequestDto.setImageUrl(fileUrl);
            }  catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        TeamUpdateResponseDto updatedTeam = teamService.updateTeam(teamId, teamUpdateRequestDto);
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    //하나의 팀에 소속된 전체 유저 목록 조회
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UsersInTeamResponseDto>> getUsersInTeam(@PathVariable Long teamId) {
        List<UsersInTeamResponseDto> members = teamService.getUsersInTeam(teamId);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
    //유저가 속한 전체 팀 목록 조회
    @GetMapping("/users/{userId}/teams")
    public ResponseEntity<List<CustomResponseTeamDto>> getTeamsByUserId(@PathVariable Long userId) {
        log.info("API /user/{}/all called", userId);
        List<CustomResponseTeamDto> teams = teamService.getTeamsByUserId(userId);
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }
}



