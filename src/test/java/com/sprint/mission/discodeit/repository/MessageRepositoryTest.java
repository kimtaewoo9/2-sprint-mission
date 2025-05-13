package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private User testUser;
    private Channel testChannel;

    @BeforeEach
    void setUp() {
        testUser = User.createUser("messageAuthor", "author@example.com", "password");
        entityManager.persist(testUser);

        testChannel = Channel.createPublicChannel("messageChannel", "Channel for testing messages");
        entityManager.persist(testChannel);

        entityManager.flush();
    }

    private Message createAndPersistMessage(String content, User author, Channel channel,
        Instant createdAt) {
        Message message = Message.createMessage(channel, author, content);
        if (createdAt != null) {
            message.setCreatedAt(createdAt); // 테스트를 위한 시간 명시적 설정
        }
        return entityManager.persistAndFlush(message);
    }

    @Test
    @DisplayName("메시지 저장 및 ID로 조회 (성공 및 실패)")
    void saveAndFindById_SuccessAndNotFound() {
        Message newMessage = createAndPersistMessage("Hello, World!", testUser, testChannel,
            Instant.now());
        entityManager.clear();

        Optional<Message> foundOptional = messageRepository.findById(newMessage.getId());

        assertThat(foundOptional).isPresent();
        Message foundMessage = foundOptional.get();
        assertThat(foundMessage.getContent()).isEqualTo("Hello, World!");
        assertThat(foundMessage.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(foundMessage.getChannel().getId()).isEqualTo(testChannel.getId());
        assertThat(foundMessage.isNew()).isFalse();

        Optional<Message> notFoundOptional = messageRepository.findById(UUID.randomUUID());
        assertThat(notFoundOptional).isNotPresent();
    }

    @Test
    @DisplayName("findAll 페이징 및 createdAt 역순 정렬 (성공 및 결과 없음)")
    void findAll_WithPagingAndSorting_SuccessAndEmpty() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> emptyPage = messageRepository.findAll(pageRequest);
        assertThat(emptyPage.getContent()).isEmpty();

        Message message1 = createAndPersistMessage("First Message", testUser, testChannel,
            Instant.now().minusSeconds(100));
        Message message2 = createAndPersistMessage("Second Message", testUser, testChannel,
            Instant.now().minusSeconds(50));
        Message message3 = createAndPersistMessage("Third Message", testUser, testChannel,
            Instant.now());
        entityManager.clear();

        Page<Message> messagePage = messageRepository.findAll(pageRequest);

        assertThat(messagePage.getContent()).hasSize(2);
        assertThat(messagePage.getContent().get(0).getContent()).isEqualTo("Third Message");
        assertThat(messagePage.getContent().get(1).getContent()).isEqualTo("Second Message");
        assertThat(messagePage.getTotalElements()).isEqualTo(3);
        assertThat(messagePage.getTotalPages()).isEqualTo(2);
        assertThat(messagePage.hasNext()).isTrue();
    }

    @Test
    @DisplayName("메시지 삭제 (성공)")
    void deleteMessage_Success() {
        Message messageToDelete = createAndPersistMessage("Message to delete", testUser,
            testChannel, Instant.now());
        UUID messageId = messageToDelete.getId();
        entityManager.clear();

        assertThat(messageRepository.existsById(messageId)).isTrue();
        messageRepository.deleteById(messageId);
        entityManager.flush();
        entityManager.clear();

        assertThat(messageRepository.existsById(messageId)).isFalse();
    }
}
