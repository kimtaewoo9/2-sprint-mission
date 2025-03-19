package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public UUID createPublicChannel(CreateChannelRequest request) {
        Channel channel = new Channel(request.getChannelName(), ChannelType.PUBLIC);
        channelRepository.save(channel);

        return channel.getId();
    }

    @Override
    public UUID createPrivateChannel(CreateChannelRequest request, List<UUID> userIds) {
        Channel channel = new Channel(null, ChannelType.PRIVATE);

        //  read status -> 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델입니다. 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다
        for (UUID userId : userIds) {
            User user = userRepository.findByUserId(userId);
            ReadStatus readStatus = new ReadStatus(channel.getId(), user.getId());
            readStatusRepository.save(readStatus);

            channel.getUserIds().add(user.getId());
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
                userIds = new ArrayList<>();
                userIds.addAll(channel.getUserIds());
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

        channel.updateName(request.getChannelName());
        channel.updateChannelType(request.getChannelType());

        channelRepository.save(channel);
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
}
