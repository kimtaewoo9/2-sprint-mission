package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void create(CreateMessageRequest request) {
        validateContent(request.getContent());
        validateUserIdAndChannelId(request.getSenderId(), request.getChannelId());

        Message message = new Message(request.getContent(), request.getSenderId(),
            request.getChannelId());
        messageRepository.save(message);
    }

    public void create(CreateMessageRequest request,
        List<CreateBinaryContentRequest> binaryContents) {
        validateContent(request.getContent());
        validateUserIdAndChannelId(request.getSenderId(), request.getChannelId());

        Message message = new Message(request.getContent(), request.getSenderId(),
            request.getChannelId());
        messageRepository.save(message);

        if (binaryContents == null || binaryContents.isEmpty()) {
            throw new IllegalArgumentException("[ERROR]binary content is empty");
        }

        List<UUID> binaryContentIds = new ArrayList<>();
        for (CreateBinaryContentRequest binaryContentDto : binaryContents) {
            BinaryContent content = new BinaryContent(binaryContentDto.getBinaryImage());

            binaryContentRepository.save(content);
            binaryContentIds.add(content.getId());
        }

        update(message.getId(),
            new UpdateMessageRequest(message.getContent(), binaryContentIds));
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findByMessageId(messageId);
    }

    @Override
    public List<MessageResponseDto> findAll() {
        return messageRepository.findAll().stream()
            .map(MessageResponseDto::from).toList();
    }

    @Override
    public List<MessageResponseDto> findByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
            .filter(m -> m.getChannelId().equals(channelId))
            .map(MessageResponseDto::from).toList();
    }

    @Override
    public void update(UUID messageId, UpdateMessageRequest request) {
        Message message = findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("[ERROR] message id not found");
        }

    }

    @Override
    public void remove(UUID messageId) {
        Message message = messageRepository.findByMessageId(messageId);

        List<UUID> attachedImageIds = message.getAttachedImageIds();
        for (UUID id : attachedImageIds) {
            binaryContentRepository.delete(id);
        }

        messageRepository.delete(messageId);
    }

    private void validateUserIdAndChannelId(UUID userId, UUID channelId) {
        try {
            userService.findByUserId(userId);
            channelService.findByChannelId(channelId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("사용자 정보 또는 채널 정보를 확인할 수 없습니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 없습니다. 메시지를 입력해주세요.");
        }
    }
}
