package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.custom.DuplicateResourceException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<UserStatus> userStatusCaptor;
    @Captor
    ArgumentCaptor<BinaryContent> binaryContentCaptor;
    @Captor
    ArgumentCaptor<UUID> uuidCaptor;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @InjectMocks
    private BasicUserService basicUserService;
    private User testUser;
    private UserDto testUserDto;
    private BinaryContent testProfile;
    private BinaryContentDto testProfileDto;
    private UUID testUserId;
    private UUID testProfileId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testProfileId = UUID.randomUUID();

        // 테스트 전반에 사용될 기본 Mock 객체들 설정
        testUser = User.createUser("testuser", "test@example.com", "password");
        testUser.setId(testUserId); // ID 할당 가정

        testProfile = BinaryContent.createBinaryContent("profile.jpg", 100L, "image/jpeg");
        testProfile.setId(testProfileId);

        testProfileDto = new BinaryContentDto(testProfileId, "profile.jpg", 100L, "image/jpeg",
            null);
        testUserDto = new UserDto(testUserId, "testuser", "test@example.com", null,
            true); // 기본 DTO (프로필 없음)
    }

    // ========================== CREATE Tests ==========================
    @Nested
    @DisplayName("UserService: create 메소드")
    class CreateUserTests {

        @Test
        @DisplayName("성공: 프로필 없이 사용자 생성")
        void createUser_Success() {
            // Given
            CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com",
                "password123");
            // UserStatus는 내부적으로 생성되므로, save 호출만 검증
            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(testUser); // 저장 후 testUser 반환 가정
            given(userStatusRepository.save(any(UserStatus.class))).willReturn(
                new UserStatus()); // 저장 성공 가정
            given(userMapper.toDto(testUser)).willReturn(testUserDto); // 저장된 user 객체로 DTO 변환 가정

            // When
            UserDto result = basicUserService.create(request);

            // Then
            assertThat(result).isEqualTo(testUserDto);
            then(userRepository).should().save(userCaptor.capture());
            assertThat(userCaptor.getValue().getUsername()).isEqualTo(request.username());
            then(userStatusRepository).should().save(any(UserStatus.class));
            then(userMapper).should().toDto(testUser);
        }

        @Test
        @DisplayName("실패: 사용자 이름 중복")
        void createUser_Fail_UsernameExists() {
            // Given
            CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com",
                "password123");
            given(userRepository.existsByUsername(request.username())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> basicUserService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이메일 중복")
        void createUser_Fail_EmailExists() {
            // Given
            CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com",
                "password123");
            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userRepository.existsByEmail(request.email())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> basicUserService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
            then(userRepository).should(never()).save(any(User.class));
        }
    }

    // ========================== UPDATE Tests ==========================
    @Nested
    @DisplayName("UserService: update 메소드")
    class UpdateUserTests {

        @Test
        @DisplayName("성공: 프로필 없이 사용자 정보 수정")
        void updateUser_Success() {
            // Given
            UpdateUserRequest request = new UpdateUserRequest("updateduser", "up@example.com",
                "updatedpass");
            UserDto updatedDto = new UserDto(testUserId, request.newUsername(), request.newEmail(),
                null, true);

            given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
            given(userMapper.toDto(testUser)).willReturn(
                updatedDto); // 업데이트된 testUser가 DTO로 변환될 것 가정

            // When
            UserDto result = basicUserService.update(testUserId, request);

            // Then
            assertThat(result).isEqualTo(updatedDto);
            then(userRepository).should().findById(testUserId);
            // testUser 객체의 update 메소드가 호출되었는지 간접 확인 (변경된 값 비교)
            assertThat(testUser.getUsername()).isEqualTo(request.newUsername());
            assertThat(testUser.getEmail()).isEqualTo(request.newEmail());
            assertThat(testUser.getPassword()).isEqualTo(request.newPassword());
            then(userMapper).should().toDto(testUser);
        }

        @Test
        @DisplayName("실패: 사용자 정보 수정 시 사용자 없음")
        void updateUser_Fail_NotFound() {
            // Given
            UpdateUserRequest request = new UpdateUserRequest("updateduser", "up@example.com",
                "updatedpass");
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicUserService.update(testUserId, request))
                .isInstanceOf(NoSuchElementException.class);
            then(userMapper).should(never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("실패: 프로필 포함 사용자 정보 수정 시 사용자 없음")
        void updateUserWithProfile_Fail_NotFound() {
            // Given
            UpdateUserRequest userRequest = new UpdateUserRequest("upuser", "up@example.com",
                "uppass");
            CreateBinaryContentRequest profileRequest = new CreateBinaryContentRequest("new.png",
                "image/png", new byte[]{1});
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(
                () -> basicUserService.update(testUserId, userRequest, profileRequest))
                .isInstanceOf(NoSuchElementException.class);
            then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
        }
    }

    // ========================== REMOVE Tests ==========================
    @Nested
    @DisplayName("UserService: remove (delete) 메소드")
    class RemoveUserTests {

        @Test
        @DisplayName("성공: 프로필 없는 사용자 삭제")
        void removeUser_Success_NoProfile() {
            // Given
            given(userRepository.findById(testUserId)).willReturn(
                Optional.of(testUser)); // testUser는 프로필 없음
            willDoNothing().given(userRepository).delete(testUser);

            // When
            basicUserService.remove(testUserId);

            // Then
            then(userRepository).should().findById(testUserId);
            then(binaryContentStorage).should(never()).deleteById(any(UUID.class));
            then(userRepository).should().delete(testUser);
        }

        @Test
        @DisplayName("성공: 프로필 있는 사용자 삭제")
        void removeUser_Success_WithProfile() {
            // Given
            testUser.setProfile(testProfile); // 사용자에게 프로필 설정
            given(userRepository.findById(testUserId)).willReturn(Optional.of(testUser));
            willDoNothing().given(binaryContentStorage).deleteById(testProfileId);
            willDoNothing().given(userRepository).delete(testUser);

            // When
            basicUserService.remove(testUserId);

            // Then
            then(userRepository).should().findById(testUserId);
            then(binaryContentStorage).should().deleteById(testProfileId); // 프로필 삭제 확인
            then(userRepository).should().delete(testUser);
        }

        @Test
        @DisplayName("실패: 사용자 없음")
        void removeUser_Fail_NotFound() {
            // Given
            given(userRepository.findById(testUserId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> basicUserService.remove(testUserId))
                .isInstanceOf(NoSuchElementException.class);
            then(userRepository).should(never()).delete(any(User.class));
            then(binaryContentStorage).should(never()).deleteById(any(UUID.class));
        }
    }
}
