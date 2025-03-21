package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.CreateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    UUID createPublicChannel(CreateChannelRequest request);

    UUID createPrivateChannel(CreateChannelRequest request, List<UUID> userIds);

    ChannelResponseDto findByChannelId(UUID channelId);

    List<ChannelResponseDto> findAllByUserId(UUID userId);

    void update(UUID channelId, UpdateChannelRequest request);

    void remove(UUID channelId);

    void addUser(UUID channelId, UUID userId);
}
