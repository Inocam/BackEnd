package com.sparta.backend.workspace.service;

<<<<<<< HEAD
import com.sparta.backend.workspace.dto.*;
import com.sparta.backend.workspace.entity.Invitation;
import com.sparta.backend.workspace.entity.Team;
import com.sparta.backend.workspace.entity.TeamUser;
import com.sparta.backend.workspace.exception.CustomException;
import com.sparta.backend.workspace.repository.InvitationRepository;
import com.sparta.backend.workspace.repository.TeamRepository;
import com.sparta.backend.workspace.repository.TeamUserRepository;
import com.sparta.backend.user.model.User;
import com.sparta.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// 단위 테스트 : 데이터베이스와 상호작용  X , 특정 로직을 뻐르고 독립적으로 테스트하기 위해서 모의 데이터 사용
class TeamServiceTest {
    //실체 객체 x, 가짜 객체(모의 객체)를 사용함  -> @Mock 을 사용해서 모의함
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamUserRepository teamUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private S3ImageService s3ImageService;

    @Mock
    private MultipartFile image;

    @InjectMocks    //teamService 에 모의된 객체들을 주입함
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("팀 생성 성공")
    void createTeamSuccess() {
        // given(테스트 대상을 실제로 실행하기 전에 테스트에 필요한 값(상태)을 미리 선언해둠)
        RequestTeamDto requestTeamDto = new RequestTeamDto();   //클래스의 새 인스턴스 생성
        requestTeamDto.setName("Test Team");    //생성할 팀의 이름을 Test team 으로 설정
        requestTeamDto.setDescription("Test Description");
        requestTeamDto.setCreatorId(1L);    //팀의 생성자 id를 1L 로 설정 / creatorId는 데이터베이스에서 해당 사용자를 식별하는 데 사용

        User user = new User(); //팀의 생성자 정보 // 모의 데이터 생성
        user.setId(1L);
        user.setUsername("testuser");

        Team team = new Team(requestTeamDto);
        team.setTeamId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));    //실제 데이터베이스 x, mockUser 객체를 반환하도록 설정
        when(teamRepository.save(any(Team.class))).thenReturn(team);        //'-> 데이터베이스에 연결되지 않은 상태에서도 서비스 로직 테스트 가능

        // when(테스트 하고자하는 대상을 실제로 실행시팀)
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto, image);

        // then(예상되는 결과에 대해 예측, 맞는지 확인)
        assertEquals("Test Team", responseTeamDto.getName());
        assertEquals("testuser", responseTeamDto.getCreatorName());
        verify(teamRepository, times(1)).save(any(Team.class));
        verify(teamUserRepository, times(1)).save(any(TeamUser.class));
    }

    @Test
    @DisplayName("팀 생성 시 사용자 없음 예외 발생")
    void createTeamUserNotFound() {
        // given
        RequestTeamDto requestTeamDto = new RequestTeamDto();
        requestTeamDto.setName("Test Team");
        requestTeamDto.setDescription("Test Description");
        requestTeamDto.setCreatorId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamService.createTeam(requestTeamDto, image);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User Not Found", exception.getError());
    }
    @Test
    @DisplayName("팀원 초대 성공")
    void inviteUserToTeamSuccess() {
        // given
        Long teamId = 1L;   //temaId 변수에 할당된 long 타입의 값 = 1L
        Long userId = 2L;   //초대 받을 사용자
        Long requesterId = 1L;  //초대 요청자 (팀장)

        User requester = new User();
        requester.setId(requesterId);
        requester.setUsername("requester");

        User invitee = new User();
        invitee.setId(userId);
        invitee.setUsername("invitee");

        Team team = new Team();
        team.setTeamId(teamId);
        team.setName("Test Team");

        TeamUser teamUser = new TeamUser(requester, team, "팀장");

        // Mock 설정
        when(userRepository.findByIdAndIsDeleteFalse(requesterId)).thenReturn(Optional.of(requester)); // 팀장 찾기
        when(userRepository.findByIdAndIsDeleteFalse(userId)).thenReturn(Optional.of(invitee)); // 초대받을 사용자 찾기
        when(teamRepository.findByTeamIdAndIsDeleteFalse(teamId)).thenReturn(Optional.of(team)); // 팀 찾기
        when(invitationRepository.existsByUserAndTeam(invitee, team)).thenReturn(false); // 기존 초대장 존재 여부 확인
        when(teamUserRepository.existsByTeamAndUser(team, invitee)).thenReturn(false); // 사용자가 이미 팀에 속해 있는지 확인
        when(teamUserRepository.findByTeamAndUser(team, requester)).thenReturn(teamUser); // 팀장이 맞는지 확인

        // Mock Invitation 설정
        Invitation invitation = new Invitation(team, invitee, requesterId);
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        // 초대 요청 DTO 생성
        InvitationRequestDto invitationRequestDto = new InvitationRequestDto(teamId, userId, requesterId);

        // when
        InvitationResponseDto responseDto = teamService.inviteUserToTeam(invitationRequestDto);

        // then
        assertEquals(invitation.getId(), responseDto.getId());  //invitation.getId() : 모의된 객체에서 온 것
        verify(invitationRepository, times(1)).save(any(Invitation.class));
    }
    @Test
    @DisplayName("사용자가 초대받은 팀 목록 조회")
    void getAllTeamsByUserIdSuccess() {
        // given
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Team team1 = new Team();
        team1.setTeamId(1L);
        team1.setName("Team 1");
        team1.setDescription("Description 1");

        Team team2 = new Team();
        team2.setTeamId(2L);
        team2.setName("Team 2");
        team2.setDescription("Description 2");

        Invitation invitation1 = new Invitation(team1, user, 2L);
        invitation1.setId(100L);

        Invitation invitation2 = new Invitation(team2, user, 3L);
        invitation2.setId(101L);

        when(invitationRepository.findByUserId(userId)).thenReturn(List.of(invitation1, invitation2));
        when(userRepository.findById(team1.getCreatorId())).thenReturn(Optional.of(user));
        when(userRepository.findById(team2.getCreatorId())).thenReturn(Optional.of(user));

        // when
        List<ResponseTeamInvitationIdDto> response = teamService.getAllTeamsByUserId(userId);

        // then
        assertEquals(2, response.size());
        assertEquals("Team 1", response.get(0).getName());
        assertEquals("Team 2", response.get(1).getName());
    }

    @Test
    @DisplayName("사용자가 초대받은 팀 목록 조회 - 초대장이 없는 경우 예외 발생")
    void getAllTeamsByUserIdNoInvitations() {
        // given
        Long userId = 1L;

        // 빈 리스트 반환을 위해 List.of() 사용
        when(invitationRepository.findByUserId(userId)).thenReturn(List.of());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamService.getAllTeamsByUserId(userId);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Bad Request", exception.getError());
        assertEquals("초대장을 받은 적이 없습니다.", exception.getMessage());
    }
    @Test
    @DisplayName("초대 처리(수락)")
    void acceptInvitationSuccess() {
        // given
        Long invitationId = 1L;

        User user = new User();
        user.setId(1L);
        user.setUsername("invitee");

        Team team = new Team();
        team.setTeamId(1L);
        team.setName("Test Team");

        Invitation invitation = new Invitation(team, user, 2L);
        invitation.setId(invitationId);

        InvitationSetRequestDto requestDto = new InvitationSetRequestDto();
        requestDto.setInvitationId(invitationId);
        requestDto.setAccept(true);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(teamUserRepository.save(any(TeamUser.class))).thenReturn(new TeamUser(user, team, "팀원"));

        // when
        InvitationSetResponseDto responseDto = teamService.setInvitation(requestDto);

        // then
        assertEquals(invitationId, responseDto.getId());
        assertEquals(LocalDateTime.now().getDayOfYear(), responseDto.getInvitationReceivedAt().getDayOfYear());
        verify(teamUserRepository, times(1)).save(any(TeamUser.class));
        verify(invitationRepository, times(1)).delete(invitation);
    }

    @Test
    @DisplayName("초대 거부 성공")
    void rejectInvitationSuccess() {
        // given
        Long invitationId = 1L;

        User user = new User();
        user.setId(1L);
        user.setUsername("invitee");

        Team team = new Team();
        team.setTeamId(1L);
        team.setName("Test Team");

        Invitation invitation = new Invitation(team, user, 2L);
        invitation.setId(invitationId);

        InvitationSetRequestDto requestDto = new InvitationSetRequestDto();
        requestDto.setInvitationId(invitationId);
        requestDto.setAccept(false);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));

        // when
        InvitationSetResponseDto responseDto = teamService.setInvitation(requestDto);

        // then
        assertEquals(invitationId, responseDto.getId());
        verify(teamUserRepository, never()).save(any(TeamUser.class));
        verify(invitationRepository, times(1)).delete(invitation);
    }

    @Test
    @DisplayName("잘못된 초대 ID 예외 발생")
    void invitationNotFound() {
        // given
        Long invitationId = 1L;

        InvitationSetRequestDto requestDto = new InvitationSetRequestDto();
        requestDto.setInvitationId(invitationId);
        requestDto.setAccept(true);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamService.setInvitation(requestDto);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("잘못된 초대 ID 입니다.", exception.getMessage());
    }
    @Test
    @DisplayName("팀원 삭제 성공")
    void removeTeamMemberSuccess() {
        // given
        Long teamId = 1L;
        Long userId = 2L;
        Long requesterId = 3L;

        User requester = new User();
        requester.setId(requesterId);
        requester.setUsername("requester");

        User user = new User();
        user.setId(userId);
        user.setUsername("user");

        Team team = new Team();
        team.setTeamId(teamId);
        team.setName("Test Team");

        when(userRepository.findByIdAndIsDeleteFalse(requesterId)).thenReturn(Optional.of(requester));
        when(teamRepository.findByTeamIdAndIsDeleteFalse(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findByIdAndIsDeleteFalse(userId)).thenReturn(Optional.of(user));
        when(teamUserRepository.findByTeamAndUser(team, requester)).thenReturn(new TeamUser(requester, team, "팀장"));

        // when
        String result = teamService.removeTeamMember(teamId, userId, requesterId);

        // then
        assertEquals("팀원이 삭제되었습니다.", result);
        verify(teamUserRepository, times(1)).deleteByTeamAndUser(team, user);
    }

    @Test
    @DisplayName("팀장이 아닌 사용자가 팀원을 삭제하려고 할 때 예외 발생")
    void removeTeamMemberNotLeader() {
        // given
        Long teamId = 1L;
        Long userId = 2L;
        Long requesterId = 3L;

        User requester = new User();
        requester.setId(requesterId);
        requester.setUsername("requester");

        Team team = new Team();
        team.setTeamId(teamId);
        team.setName("Test Team");

        when(userRepository.findByIdAndIsDeleteFalse(requesterId)).thenReturn(Optional.of(requester));
        when(teamRepository.findByTeamIdAndIsDeleteFalse(teamId)).thenReturn(Optional.of(team));
        when(teamUserRepository.findByTeamAndUser(team, requester)).thenReturn(new TeamUser(requester, team, "팀원"));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            teamService.removeTeamMember(teamId, userId, requesterId);
        });

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Unauthorized", exception.getError());
        assertEquals("팀장만 팀원을 삭제할 수 있습니다.", exception.getMessage());
        verify(teamUserRepository, never()).deleteByTeamAndUser(any(), any());
    }
}


=======
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {

    @Test
    void createTeam() {
    }

    @Test
    void inviteUserToTeam() {
    }

    @Test
    void isRequesterTeamLeader() {
    }

    @Test
    void getAllTeamsByUserId() {
    }

    @Test
    void setInvitation() {
    }

    @Test
    void removeTeamMember() {
    }

    @Test
    void removeTeam() {
    }

    @Test
    void transferTeamLeader() {
    }

    @Test
    void updateTeam() {
    }

    @Test
    void getUsersInTeam() {
    }

    @Test
    void getTeamsByUserId() {
    }
}
>>>>>>> main
