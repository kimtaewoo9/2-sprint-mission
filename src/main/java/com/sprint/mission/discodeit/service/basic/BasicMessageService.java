package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Primary
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    private final MessageMapper messageMapper;

    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public MessageDto create(CreateMessageRequest request) {
        UUID authorId = request.authorId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(authorId).orElseThrow(
            () -> new NoSuchElementException("[ERROR] user not found")
        );

        Channel channel = channelRepository.findById(channelId).orElseThrow(
            () -> new NoSuchElementException("[ERROR] channel not found")
        );

        // read status 활용해서 권한 체크 .
        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(channelId, authorId);
        if (readStatus == null) {
            throw new IllegalArgumentException("[ERROR] user has no access to this channel");
        }

        String content = request.content();
        Message message = Message.createMessage(channel, user, content);

        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public MessageDto create(CreateMessageRequest request,
        List<CreateBinaryContentRequest> binaryContents) {

        UUID authorId = request.authorId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(authorId).orElseThrow(
            () -> new NoSuchElementException("[ERROR] user not found")
        );

        Channel channel = channelRepository.findById(channelId).orElseThrow(
            () -> new NoSuchElementException("[ERROR] channel not found")
        );

        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(channelId, authorId);
        if (readStatus == null) {
            throw new IllegalArgumentException("[ERROR] user has no access to this channel");
        }

        String content = request.content();
        Message message = Message.createMessage(channel, user, content);

        if (binaryContents == null || binaryContents.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] binary content is empty");
        }

        for (CreateBinaryContentRequest binaryContentDto : binaryContents) {
            String fileName = binaryContentDto.fileName();
            String contentType = binaryContentDto.contentType();
            byte[] bytes = binaryContentDto.bytes();
            long size = bytes.length;

            BinaryContent binaryContent = BinaryContent.createBinaryContent(fileName, size,
                contentType);
            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(savedBinaryContent.getId(), bytes);

            message.addBinaryContent(binaryContent);
        }
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public MessageDto findById(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NoSuchElementException("[ERROR] message not found");
        }

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public List<MessageDto> findAll() {
        return messageRepository.findAll().stream()
            .map(messageMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<MessageDto> findByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
            .stream().map(messageMapper::toDto).toList();
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, UpdateMessageRequest request) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NoSuchElementException("[ERROR] message not found");
        }

        String content = request.newContent();
        message.update(content);

        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void remove(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new NoSuchElementException("[ERROR] message not found");
        }

        List<BinaryContent> attachments = message.getAttachments();
        for (BinaryContent attachment : attachments) {
            binaryContentStorage.deleteById(attachment.getId());
        }

        messageRepository.deleteById(messageId);
    }
}
