package com.sparta.backend.workspace.service;


import com.sparta.backend.user.model.UserRoleEnum;
import com.sparta.backend.user.repository.UserRepository;
import com.sparta.backend.workspace.dto.RequestTeamDto;
import com.sparta.backend.workspace.dto.ResponseTeamDto;
import com.sparta.backend.workspace.entity.Team;
import com.sparta.backend.user.model.User;
import com.sparta.backend.workspace.repository.TeamRepository;
import com.sparta.backend.workspace.repository.TeamUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeamServiceIntegrationTest {

    @Autowired
    TeamService teamService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamUserRepository teamUserRepository;

    User user;
    Team team;

    @BeforeEach
    void setUp() {
        // 테스트 전에 필요한 사용자 데이터를 설정합니다.
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password"); // 비밀번호 추가, 필요에 따라 해싱된 비밀번호를 사용해야 할 수도 있음
        user.setRole(UserRoleEnum.USER);    //UserRoleEnum은 열거형(enum)으로 정의되어 있어서, 이 열거형을 사용하여 role 필드를 설정해야함.
        userRepository.save(user);
    }
//통합 테스트 : 실제 DB를 포함한 애플리케이션의 여러 구성 요소들이 서로 어떻게 상호작용하는지를 테스트함
// '-> 실제 데이터베이스에 데이터를 저장하고, 그 데이터를 사용하여 로직을 테스트 / 모의 데이터를 사용하는 단위 테스트랑 다름
    @Test
    @Order(1)
    @DisplayName("팀 생성 통합 테스트")
    void createTeamTest() {
        //given
        RequestTeamDto requestTeamDto = new RequestTeamDto();
        requestTeamDto.setName("Test Team");
        requestTeamDto.setDescription("Test Description");
        requestTeamDto.setCreatorId(user.getId());  // 기존에 저장한 사용자의 ID를 사용합니다.

        //when
        ResponseTeamDto responseTeamDto = teamService.createTeam(requestTeamDto, null); //이미지 없이도 팀 생성 되는지 검증

        //then
        assertNotNull(responseTeamDto);
        assertEquals("Test Team", responseTeamDto.getName());
        assertEquals("Test Description", responseTeamDto.getDescription());

        //추가 검증 : 데이터 베이스테 저장된 팀 확인
        team = teamRepository.findById(responseTeamDto.getTeamId()).orElse(null);
        assertNotNull(team);
        assertEquals("Test Team", team.getName());

    }
}
