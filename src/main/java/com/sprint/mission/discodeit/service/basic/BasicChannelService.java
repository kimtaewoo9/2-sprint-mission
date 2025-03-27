package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public UUID createPublicChannel(CreatePublicChannelRequest request) {
        String name = request.getChannelName();
        String description = request.getDescription();
        Channel channel = new Channel(name, description, ChannelType.PUBLIC);

        channelRepository.save(channel);

        return channel.getId();
    }

    @Override
    public UUID createPrivateChannel(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);

        List<UUID> userIds = request.getParticipantIds();

        for (UUID userId : userIds) {

            ReadStatus readStatus = new ReadStatus(channel.getId(), userId);
            readStatusRepository.save(readStatus);

            channel.getUserIds().add(userId);
        }

        channelRepository.save(channel);

        return channel.getId();
    }

    @Override
    public ChannelResponseDto findByChannelId(UUID channelId) {
        Channel channel = channelRepository.findByChannelId(channelId);

        List<UUID> userIds = channel.getUserIds();
        Instant lastMessageTimestamp = getLastMessageTimestamp(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            return ChannelResponseDto.from(channel, lastMessageTimestamp, userIds);
        } else {
            return ChannelResponseDto.from(channel, lastMessageTimestamp, null);
        }
    }

    private Instant getLastMessageTimestamp(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo).orElse(null);
    }

    @Override
    public List<ChannelResponseDto> findAll() {
        List<ChannelResponseDto> channelResponseDtos = new ArrayList<>();

        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            ChannelResponseDto responseDto = ChannelResponseDto.from(channel,
                getLastMessageTimestamp(channel.getId()),
                channel.getUserIds());
            channelResponseDtos.add(responseDto);
        }

        return channelResponseDtos;
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponseDto> channelResponseDtos = new ArrayList<>();

        for (Channel channel : channels) {
            if (channel.getType() == ChannelType.PRIVATE && channel.getUserIds().
                stream().noneMatch(id -> id.equals(userId))) {
                continue;
            }

            List<UUID> userIds = null;
            if (channel.getType() == ChannelType.PRIVATE) {
                userIds = new ArrayList<>(channel.getUserIds());
            }

            channelResponseDtos.add(
                ChannelResponseDto.from(channel, getLastMessageTimestamp(channel.getId()),
                    userIds));
        }

        return channelResponseDtos;
    }

    @Override
    public void update(UUID channelId, UpdateChannelRequest request) {
        Channel channel = channelRepository.findByChannelId(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("[ERROR] channel not found");
        }
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("[ERROR] private channel cannot be modified");
        }

        String newName = request.getChannelName();
        String newDescription = request.getDescription();

        channel.updateName(newName);
        channel.updateDescription(newDescription);

//        channelRepository.save(channel);
    }

    @Override
    public void remove(UUID channelId) {
        Channel channel = channelRepository.findByChannelId(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("[ERROR] channel not found");
        }

        messageRepository.findAllByChannelId(channelId).
            forEach(m -> messageRepository.delete(m.getId()));
        readStatusRepository.findAllByChannelId(channelId).
            forEach(rs -> readStatusRepository.delete(rs.getId()));

        channelRepository.delete(channelId);
    }

    @Override
    public void addUser(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findByChannelId(channelId);
        channel.addUser(userId);
        channelRepository.save(channel);
    }
}
