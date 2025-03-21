package com.sprint.mission.discodeit.service.file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @InjectMocks // @Mock 으로 생성된 객체를 자동으로 주입해주는 애노테이션
    private FileUserService userService;

    @Test
    @DisplayName("유저 생성 테스트")
    void 유저생성() throws Exception {

        CreateUserRequest request = new CreateUserRequest("김태우",
            "bezzi1654@naver.com",
            "password1");
        // 이메일 중복 체크를 위한 빈 리스트 반환 .
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        UUID userId = userService.create(request);
        Assertions.assertThat(userId).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("이메일 중복으로 인한 생성 실패")
    void 유저생성실패() throws Exception {

        CreateUserRequest request = new CreateUserRequest("김태우",
            "bezzi1654@kookmin.ac.kr",
            "password1");

        List<User> users = List.of(
            new User("user1", "bezzi1654@kookmin.ac.kr", "password1")
        );
        when(userRepository.findAll()).thenReturn(users);

        Assertions.assertThatThrownBy(() -> userService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 중복된 이메일 입니다.");
    }

    @Test
    @DisplayName("유저 조회 테스트")
    void 유저조회테스트() throws Exception {

        UUID userId = UUID.randomUUID(); // 아무거나 만들어
        User user = new User("김태우",
            "bezzi1654@kookmin.ac.kr",
            "password1");
        UserStatus userStatus = new UserStatus(userId);

        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userStatusRepository.findByUserId(userId)).thenReturn(userStatus);

        UserResponseDto result = userService.findByUserId(userId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName())
            .isEqualTo("김태우");
        Assertions.assertThat(result.getEmail())
            .isEqualTo("bezzi1654@kookmin.ac.kr");
    }

    @Test
    @DisplayName("전체 유저 조회")
    void findAllSuccess() throws Exception {
        List<User> users = List.of(
            new User("user1", "user1@email.com", "password1"),
            new User("user2", "user2@email.com", "password2")
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userStatusRepository.findByUserId(any())).thenReturn(
            new UserStatus(UUID.randomUUID()));

        List<UserResponseDto> result = userService.findAll();

        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("수정 하기")
    void 유저수정() throws Exception {

        // given
        User user = new User("김태우",
            "bezzi1654@kookmin.ac.kr",
            "pasword123");

        // when
        when(userRepository.findByUserId(any())).thenReturn(user);
        UpdateUserRequest request = new UpdateUserRequest("user1",
            "user1@email.com",
            "password1");
        userService.update(user.getId(), request);

        // then
        Assertions.assertThat(userRepository.findByUserId(user.getId()).getName())
            .isEqualTo("user1");
        Assertions.assertThat(userRepository.findByUserId(user.getId()).getEmail())
            .isEqualTo("user1@email.com");
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    void deleteUserTest() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        User user = new User("김태우",
            "bezzi1654@naver.com",
            "password1");

        // when
        when(userRepository.findByUserId(userId)).thenReturn(user);
        userService.remove(userId);

        // then
        verify(userRepository).findByUserId(userId);
        verify(userRepository).delete(userId); // findByUserId와 delete가 실행 됐는가 .
    }
}
