package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(BasicMessageService.class);

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

        logger.info("Creating new message in channel: {} by user: {}", channelId, authorId);
        logger.debug("Message content length: {}", request.content().length());

        try {
            User user = userRepository.findById(authorId).orElseThrow(
                () -> {
                    logger.warn("User not found with ID: {}", authorId);
                    return new NoSuchElementException("[ERROR] user not found");
                }
            );

            Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> {
                    logger.warn("Channel not found with ID: {}", channelId);
                    return new NoSuchElementException("[ERROR] channel not found");
                }
            );

            String content = request.content();
            Message message = Message.createMessage(channel, user, content);

            Message savedMessage = messageRepository.save(message);
            logger.info("Message created successfully with ID: {}", savedMessage.getId());

            return messageMapper.toDto(message);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to create message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during message creation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MessageDto create(CreateMessageRequest request,
        List<CreateBinaryContentRequest> binaryContents) {

        UUID authorId = request.authorId();
        UUID channelId = request.channelId();

        logger.info("Creating new message with attachments in channel: {} by user: {}",
            channelId, authorId);
        logger.debug("Message content length: {}, attachment count: {}",
            request.content().length(), binaryContents != null ? binaryContents.size() : 0);

        try {
            User user = userRepository.findById(authorId).orElseThrow(
                () -> {
                    logger.warn("User not found with ID: {}", authorId);
                    return new NoSuchElementException("[ERROR] user not found");
                }
            );

            Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> {
                    logger.warn("Channel not found with ID: {}", channelId);
                    return new NoSuchElementException("[ERROR] channel not found");
                }
            );

            String content = request.content();
            Message message = Message.createMessage(channel, user, content);

            if (binaryContents == null || binaryContents.isEmpty()) {
                logger.warn("Binary content list is empty for message with attachments");
                throw new IllegalArgumentException("[ERROR] binary content is empty");
            }

            for (CreateBinaryContentRequest binaryContentDto : binaryContents) {
                String fileName = binaryContentDto.fileName();
                String contentType = binaryContentDto.contentType();
                byte[] bytes = binaryContentDto.bytes();
                long size = bytes.length;

                logger.debug("Adding attachment: {}, size: {} bytes, type: {}",
                    fileName, size, contentType);

                BinaryContent binaryContent = BinaryContent.createBinaryContent(fileName, size,
                    contentType);
                BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(savedBinaryContent.getId(), bytes);

                message.addBinaryContent(binaryContent);
            }
            Message savedMessage = messageRepository.save(message);

            logger.info(
                "Message with attachments created successfully with ID: {}, attachment count: {}",
                savedMessage.getId(), message.getAttachments().size());

            return messageMapper.toDto(message);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            logger.warn("Failed to create message with attachments: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during message with attachments creation: {}",
                e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MessageDto findById(UUID messageId) {
        logger.info("Finding message by ID: {}", messageId);

        try {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message == null) {
                logger.warn("Message not found with ID: {}", messageId);
                throw new NoSuchElementException("[ERROR] message not found");
            }

            logger.debug("Message found: author={}, channel={}, created at={}",
                message.getAuthor().getUsername(),
                message.getChannel().getId(),
                message.getCreatedAt());

            return messageMapper.toDto(message);
        } catch (NoSuchElementException e) {
            logger.warn("Message lookup failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during message lookup: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
        logger.info("Finding messages for channel: {}, cursor: {}, size: {}",
            channelId, cursor, size);

        try {
            Pageable pageable = PageRequest.of(0, size, Sort.by(Direction.DESC, "createdAt"));
            Page<Message> messages;

            if (cursor == null) {
                logger.debug("Finding first page of messages for channel: {}", channelId);
                messages = messageRepository.findFirstMessages(channelId, pageable);
            } else {
                logger.debug("Finding next page of messages for channel: {} after: {}",
                    channelId, cursor);
                messages = messageRepository.findNextMessages(channelId, cursor, pageable);
            }

            boolean hasNext = messages.getContent().size() == size;
            Instant nextCursor =
                hasNext ? messages.getContent().get(messages.getContent().size() - 1).getCreatedAt()
                    : null;

            logger.info("Found {} messages for channel: {}, hasNext: {}",
                messages.getContent().size(), channelId, hasNext);

            return new PageResponse<>(messages.stream()
                .map(messageMapper::toDto)
                .toList(), nextCursor, size, hasNext, null);
        } catch (Exception e) {
            logger.error("Failed to find messages for channel {}: {}", channelId, e.getMessage(),
                e);
            throw e;
        }
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, UpdateMessageRequest request) {
        logger.info("Updating message with ID: {}", messageId);
        logger.debug("New content length: {}", request.newContent().length());

        try {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message == null) {
                logger.warn("Message not found with ID: {}", messageId);
                throw new NoSuchElementException("[ERROR] message not found");
            }

            String oldContent = message.getContent();
            String newContent = request.newContent();
            message.update(newContent);

            messageRepository.save(message);

            logger.info("Message updated successfully: {}", messageId);
            logger.debug("Content updated - old length: {}, new length: {}",
                oldContent.length(), newContent.length());

            return messageMapper.toDto(message);
        } catch (NoSuchElementException e) {
            logger.warn("Message update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during message update: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void remove(UUID messageId) {
        logger.info("Removing message with ID: {}", messageId);

        try {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message == null) {
                logger.warn("Message not found with ID: {}", messageId);
                throw new NoSuchElementException("[ERROR] message not found");
            }

            logger.debug("Removing message: author={}, channel={}, created at={}",
                message.getAuthor().getUsername(),
                message.getChannel().getId(),
                message.getCreatedAt());

            List<BinaryContent> attachments = message.getAttachments();
            if (!attachments.isEmpty()) {
                logger.debug("Removing {} attachments for message {}", attachments.size(),
                    messageId);
                for (BinaryContent attachment : attachments) {
                    logger.debug("Deleting attachment: {}, ID: {}",
                        attachment.getFileName(), attachment.getId());
                    binaryContentStorage.deleteById(attachment.getId());
                }
            }

            messageRepository.deleteById(messageId);
            logger.info("Message removed successfully: {}", messageId);
        } catch (NoSuchElementException e) {
            logger.warn("Message removal failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during message removal: {}", e.getMessage(), e);
            throw e;
        }
    }
}
