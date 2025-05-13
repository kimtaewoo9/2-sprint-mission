package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User createUserEntity(String username, String email, String password) {
        return User.createUser(username, email, password);
    }

    @Test
    @DisplayName("사용자 저장 및 ID로 조회 성공")
    void saveAndFindById_success() {
        User newUser = createUserEntity("testuser", "test@example.com", "password123");

        User savedUser = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        Optional<User> foundUserOptional = userRepository.findById(savedUser.getId());

        assertThat(foundUserOptional).isPresent();
        User foundUser = foundUserOptional.get();
        assertThat(foundUser.getId()).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(foundUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(foundUser.isNew()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 시 빈 Optional 반환")
    void findById_whenUserDoesNotExist_shouldReturnEmpty() {
        Optional<User> foundUserOptional = userRepository.findById(UUID.randomUUID());
        assertThat(foundUserOptional).isNotPresent();
    }

    @Test
    @DisplayName("findByUsername: 사용자 이름으로 조회 성공")
    void findByUsername_success() {
        User userToSave = createUserEntity("findMe", "findme@example.com", "password123");
        entityManager.persistAndFlush(userToSave);
        entityManager.clear();

        User foundUser = userRepository.findByUsername("findMe");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("findme@example.com");
        assertThat(foundUser.isNew()).isFalse();
    }

    @Test
    @DisplayName("findByUsername: 존재하지 않는 사용자 이름으로 조회 시 null 반환")
    void findByUsername_notFound_returnsNull() {
        User foundUser = userRepository.findByUsername("nonExistentUser");
        assertThat(foundUser).isNull();
    }

    @Test
    @DisplayName("existsByEmail: 존재하는 이메일 확인 성공 (true 반환)")
    void existsByEmail_emailExists_returnsTrue() {
        User userToSave = createUserEntity("userWithEmail", "exists@example.com", "password123");
        entityManager.persistAndFlush(userToSave);
        entityManager.clear();

        boolean exists = userRepository.existsByEmail("exists@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: 존재하지 않는 이메일 확인 (false 반환)")
    void existsByEmail_emailDoesNotExist_returnsFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }
}
