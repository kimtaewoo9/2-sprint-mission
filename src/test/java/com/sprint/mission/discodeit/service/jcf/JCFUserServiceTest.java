package com.sprint.mission.discodeit.service.jcf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Arrays;
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
class JCFUserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @InjectMocks
    private JCFUserService userService;

    @Test
    @DisplayName("유저 성공 테스트")
    void 유저생성() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
            "김태우",
            "test@email.com"
            , "password123"
        );

        User expectedUser = new User(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        );

        // when
        UUID userId = userService.create(request);

        Assertions.assertThat(userId).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("중복 이메일로 유저 생성 시 예외 발생")
    void 중복유저생성() throws Exception {

        User user = new User(
            "홍길동",
            "user1@email.com"
            , "password1"
        );

        CreateUserRequest duplicatedRequest = new CreateUserRequest(
            "김태우",
            "user1@email.com", // 이메일 중복
            "password2"
        );

        //Mock userRepository에게 findAll()을 하면 user를 담은 list를 반환하게 한다.
        when(userRepository.findAll())
            .thenReturn(List.of(user));

        Assertions.assertThatThrownBy(() -> userService.create(duplicatedRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 중복된 이메일 입니다.");
    }

    @Test
    @DisplayName("유저 정보 조회 테스트")
    void findUserSuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User("김태우",
            "bezzi1654@kookmin.ac.kr",
            "password123");
        UserStatus userStatus = new UserStatus(userId); // user는 userStatus를 모름 .
        // userStatus가 userId를 참조하고 있기 때문에 .

        // Mock 객체인 userRepository에게 userId 입력하면 user를 반환해줘
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userStatusRepository.findByUserId(userId)).thenReturn(userStatus);

        UserResponseDto result = userService.findByUserId(userId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).
            isEqualTo("김태우");
        Assertions.assertThat(result.getEmail()).
            isEqualTo("bezzi1654@kookmin.ac.kr");
    }

    @Test
    @DisplayName("전체 유저 목록 조회")
    void findAllTest() throws Exception {

        User user1 = new User("홍길동", "hong@email.com", "password1");
        User user2 = new User("김철수", "kim@email.com", "password2");

        List<User> users = Arrays.asList(
            user1,
            user2
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userStatusRepository.findByUserId(any())).thenReturn(
            new UserStatus(UUID.randomUUID())); // any() -> 테스트 시 정확한 값을 몰라도 되는 상황에서 사용함 .

        List<UserResponseDto> userServiceAll = userService.findAll();

        Assertions.assertThat(userServiceAll.size()).isEqualTo(users.size());
    }
}
