package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicMessageServiceWithActualDtoTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private BasicMessageService messageService;

    private User testUserEntity;
    private Channel testChannelEntity;
    private Message testMessageEntity;
    private MessageDto testMessageDto;
    private UserDto testAuthorDto;

    private UUID userId;
    private UUID channelId;
    private UUID messageId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        testUserEntity = User.createUser("testUser", "test@example.com", "password");
        testUserEntity.setId(userId);
        testUserEntity.markNotNew();

        testChannelEntity = Channel.createPublicChannel("testChannel", "description");
        testChannelEntity.setId(channelId);
        testChannelEntity.markNotNew();

        testAuthorDto = new UserDto(userId, testUserEntity.getUsername(), testUserEntity.getEmail(),
            null, true);

        testMessageEntity = Message.createMessage(testChannelEntity, testUserEntity,
            "Initial Content");
        testMessageEntity.setId(messageId);
        testMessageEntity.setCreatedAt(Instant.now().minusSeconds(100));
        testMessageEntity.setUpdatedAt(Instant.now().minusSeconds(50));
        testMessageEntity.markNotNew();

        testMessageDto = new MessageDto(messageId, testMessageEntity.getCreatedAt(),
            testMessageEntity.getUpdatedAt(), "DTO Content", channelId, testAuthorDto,
            Collections.emptyList());
    }

    @Test
    @DisplayName("create (첨부파일 X) - 성공")
    void createMessage_success() {
        CreateMessageRequest request = new CreateMessageRequest("Hello", channelId, userId);
        Message savedMessageEntity = Message.createMessage(testChannelEntity, testUserEntity,
            request.content());
        savedMessageEntity.setId(UUID.randomUUID());
        savedMessageEntity.setCreatedAt(Instant.now());
        savedMessageEntity.setUpdatedAt(Instant.now());

        MessageDto expectedDto = new MessageDto(savedMessageEntity.getId(),
            savedMessageEntity.getCreatedAt(), savedMessageEntity.getUpdatedAt(),
            savedMessageEntity.getContent(), channelId, testAuthorDto, Collections.emptyList());

        given(userRepository.findById(userId)).willReturn(Optional.of(testUserEntity));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(testChannelEntity));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessageEntity);
        given(messageMapper.toDto(savedMessageEntity)).willReturn(expectedDto);

        MessageDto result = messageService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedDto.getId());
        assertThat(result.getContent()).isEqualTo("Hello");
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("create (첨부파일 X) - 실패: 사용자 없음")
    void createMessage_userNotFound() {
        CreateMessageRequest request = new CreateMessageRequest("Hello", channelId, userId);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(request))
            .isInstanceOf(NoSuchElementException.class);
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    @DisplayName("create (첨부파일 O) - 성공")
    void createMessageWithAttachments_success() {
        CreateMessageRequest request = new CreateMessageRequest("Hello with attachment", channelId,
            userId);
        byte[] fileBytes = "content".getBytes();
        CreateBinaryContentRequest binaryRequest = new CreateBinaryContentRequest("file.txt",
            "text/plain", fileBytes);
        List<CreateBinaryContentRequest> attachments = List.of(binaryRequest);

        Message messageToSave = Message.createMessage(testChannelEntity, testUserEntity,
            request.content());
        messageToSave.setId(UUID.randomUUID());
        messageToSave.setCreatedAt(Instant.now());
        messageToSave.setUpdatedAt(Instant.now());

        BinaryContent savedBinaryContentEntity = BinaryContent.createBinaryContent(
            binaryRequest.fileName(), (long) fileBytes.length, binaryRequest.contentType());
        savedBinaryContentEntity.setId(UUID.randomUUID());

        BinaryContentDto savedAttachmentDto = new BinaryContentDto(savedBinaryContentEntity.getId(),
            savedBinaryContentEntity.getFileName(), savedBinaryContentEntity.getSize(),
            savedBinaryContentEntity.getContentType(), null); // bytes in DTO can be null
        MessageDto expectedDto = new MessageDto(messageToSave.getId(), messageToSave.getCreatedAt(),
            messageToSave.getUpdatedAt(), messageToSave.getContent(), channelId, testAuthorDto,
            List.of(savedAttachmentDto));

        given(userRepository.findById(userId)).willReturn(Optional.of(testUserEntity));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(testChannelEntity));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(
            savedBinaryContentEntity);
        given(messageRepository.save(any(Message.class))).willReturn(messageToSave);
        given(messageMapper.toDto(any(Message.class))).willReturn(expectedDto);

        MessageDto result = messageService.create(request, attachments);

        assertThat(result).isNotNull();
        assertThat(result.getAttachments()).hasSize(1);
        assertThat(result.getAttachments().get(0).getFileName()).isEqualTo("file.txt");
        then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
        then(binaryContentStorage).should(times(1))
            .put(savedBinaryContentEntity.getId(), fileBytes);
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("create (첨부파일 O) - 실패: 채널 없음")
    void createMessageWithAttachments_channelNotFound() {
        CreateMessageRequest request = new CreateMessageRequest("Hello with attachment", channelId,
            userId);
        List<CreateBinaryContentRequest> attachments = List.of(
            new CreateBinaryContentRequest("file.txt", "text/plain", "content".getBytes())
        );
        given(userRepository.findById(userId)).willReturn(Optional.of(testUserEntity));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.create(request, attachments))
            .isInstanceOf(NoSuchElementException.class);
    }


    @Test
    @DisplayName("update - 성공")
    void updateMessage_success() {
        UpdateMessageRequest request = new UpdateMessageRequest("Updated Content");
        MessageDto updatedMessageDto = new MessageDto(messageId, testMessageEntity.getCreatedAt(),
            Instant.now(), "Updated Content", channelId, testAuthorDto, Collections.emptyList());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(testMessageEntity));
        given(messageRepository.save(any(Message.class))).willReturn(testMessageEntity);
        given(messageMapper.toDto(any(Message.class))).willReturn(updatedMessageDto);

        MessageDto result = messageService.update(messageId, request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated Content");
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        then(messageRepository).should(times(1)).save(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getContent()).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("update - 실패: 메시지 없음")
    void updateMessage_notFound() {
        UpdateMessageRequest request = new UpdateMessageRequest("Updated Content");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.update(messageId, request))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("remove - 성공")
    void removeMessage_success() {
        testMessageEntity.setAttachments(
            new java.util.ArrayList<>()); // Ensure no attachments for this test
        given(messageRepository.findById(messageId)).willReturn(Optional.of(testMessageEntity));

        messageService.remove(messageId);

        then(messageRepository).should(times(1)).deleteById(messageId);
        then(binaryContentStorage).should(never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("remove - 성공 (첨부파일 포함)")
    void removeMessage_withAttachments_success() {
        BinaryContent attachment = BinaryContent.createBinaryContent("file.txt", 10L, "text/plain");
        attachment.setId(UUID.randomUUID());
        testMessageEntity.addBinaryContent(attachment);
        given(messageRepository.findById(messageId)).willReturn(Optional.of(testMessageEntity));

        messageService.remove(messageId);

        then(messageRepository).should(times(1)).deleteById(messageId);
        then(binaryContentStorage).should(times(1)).deleteById(attachment.getId());
    }


    @Test
    @DisplayName("remove - 실패: 메시지 없음")
    void removeMessage_notFound() {
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.remove(messageId))
            .isInstanceOf(NoSuchElementException.class);
    }
}
