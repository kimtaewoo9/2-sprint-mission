package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private static final Logger logger = LoggerFactory.getLogger(BasicChannelService.class);

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(CreatePublicChannelRequest request) {
        logger.info("Creating new public channel with name: {}", request.name());
        logger.debug("Public channel description: {}", request.description());

        try {
            String name = request.name();
            String description = request.description();

            Channel channel = Channel.createPublicChannel(name, description);
            Channel savedChannel = channelRepository.save(channel);

            logger.info("Public channel created successfully with ID: {}", savedChannel.getId());
            return channelMapper.toDto(savedChannel, Collections.emptyList(), null);
        } catch (Exception e) {
            logger.error("Failed to create public channel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(CreatePrivateChannelRequest request) {
        logger.info("Creating new private channel with {} participants",
            request.participantIds().size());
        logger.debug("Private channel participant IDs: {}", request.participantIds());

        try {
            Channel channel = Channel.createPrivateChannel();
            Channel savedChannel = channelRepository.save(channel);
            logger.debug("Private channel created with ID: {}", savedChannel.getId());

            List<UserDto> participants = new ArrayList<>();

            List<UUID> participantIds = request.participantIds();
            for (UUID participantId : participantIds) {
                User user = userRepository.findById(participantId).orElseThrow(() -> {
                    logger.warn("Participant not found with ID: {}", participantId);
                    return new NoSuchElementException("[ERROR] participant not found");
                });

                participants.add(userMapper.toDto(user));

                ReadStatus readStatus = ReadStatus.createReadStatus(user, savedChannel,
                    Instant.now());
                readStatusRepository.save(readStatus);
                logger.debug("Added participant {} to channel {}", user.getUsername(),
                    savedChannel.getId());
            }

            logger.info("Private channel created successfully with ID: {} and {} participants",
                savedChannel.getId(), participants.size());
            return channelMapper.toDto(savedChannel, participants, null);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to create private channel: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during private channel creation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ChannelDto findByChannelId(UUID channelId) {
        logger.info("Finding channel by ID: {}", channelId);

        try {
            Channel channel = channelRepository.findById(channelId).orElse(null);
            if (channel == null) {
                logger.warn("Channel not found with ID: {}", channelId);
                throw new NoSuchElementException("[ERROR] channel not found");
            }

            List<UserDto> participants = new ArrayList<>();
            if (channel.getChannelType() == ChannelType.PRIVATE) {
                List<ReadStatus> readStatuses = readStatusRepository
                    .findAllByChannelId(channel.getId());

                for (ReadStatus readStatus : readStatuses) {
                    User user = readStatus.getUser();
                    UserDto userDto = userMapper.toDto(user);
                    participants.add(userDto);
                }
                logger.debug("Found {} participants in private channel {}", participants.size(),
                    channelId);
            }

            Instant lastMessageTimestamp = getLastMessageTimestamp(channelId);
            logger.debug("Channel found: {} (type: {})",
                channel.getName(), channel.getChannelType());

            return channelMapper.toDto(channel, participants, lastMessageTimestamp);
        } catch (NoSuchElementException e) {
            logger.warn("Channel lookup failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during channel lookup: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<ChannelDto> findAllByUserId(UUID userId) {
        logger.info("Finding all channels for user ID: {}", userId);

        try {
            List<UUID> subscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

            logger.debug("User {} is subscribed to {} private channels", userId,
                subscribedChannelIds.size());

            List<ChannelDto> channels = channelRepository.findAll().stream()
                .filter(channel ->
                    // PUBLIC 채널이거나 사용자가 구독한 채널만 포함
                    channel.getChannelType() == ChannelType.PUBLIC ||
                        subscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();

            logger.info("Found {} channels for user {}", channels.size(), userId);
            return channels;
        } catch (Exception e) {
            logger.error("Failed to find channels for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
            .stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(null);

        List<UserDto> participants = Collections.emptyList();

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
        }

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }

    @Override
    @Transactional
    public ChannelDto update(UUID channelId, UpdateChannelRequest request) {
        logger.info("Updating channel with ID: {}", channelId);
        logger.debug("Update details - new name: {}, new description: {}",
            request.newName(), request.newDescription());

        try {
            Channel channel = channelRepository.findById(channelId).orElse(null);
            if (channel == null) {
                logger.warn("Channel not found with ID: {}", channelId);
                throw new NoSuchElementException("[ERROR] channel not found");
            }
            if (channel.getChannelType() == ChannelType.PRIVATE) {
                logger.warn("Attempt to modify private channel: {}", channelId);
                throw new IllegalArgumentException("[ERROR] private channel cannot be modified");
            }

            String oldName = channel.getName();
            String newName = request.newName();
            String newDescription = request.newDescription();

            channel.update(newName, newDescription);

            logger.info("Channel updated successfully: {} -> {}", oldName, channel.getName());
            return channelMapper.toDto(channel, Collections.emptyList(),
                getLastMessageTimestamp(channelId));
        } catch (NoSuchElementException | IllegalArgumentException e) {
            logger.warn("Channel update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during channel update: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void remove(UUID channelId) {
        logger.info("Removing channel with ID: {}", channelId);

        try {
            if (!channelRepository.existsById(channelId)) {
                logger.warn("Channel not found with ID: {}", channelId);
                throw new NoSuchElementException("[ERROR] channel not found");
            }

            // 채널 정보 로깅을 위해 삭제 전에 조회
            Channel channel = channelRepository.findById(channelId).orElseThrow();
            logger.debug("Removing channel: {} (type: {})",
                channel.getName(), channel.getChannelType());

            int messageCount = messageRepository.countByChannelId(channelId);
            logger.debug("Deleting {} messages from channel {}", messageCount, channelId);
            messageRepository.deleteByChannelId(channelId);

            int readStatusCount = readStatusRepository.countByChannelId(channelId);
            logger.debug("Deleting {} read statuses from channel {}", readStatusCount, channelId);
            readStatusRepository.deleteByChannelId(channelId);

            channelRepository.deleteById(channelId);
            logger.info("Channel removed successfully: {}", channelId);
        } catch (NoSuchElementException e) {
            logger.warn("Channel removal failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during channel removal: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Instant getLastMessageTimestamp(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo).orElse(null);
    }
}
