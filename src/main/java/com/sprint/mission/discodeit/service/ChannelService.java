package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(String name, ChannelType type);
    Channel findByChannelId(UUID channelId);
    List<Channel> findAll();
    void update(UUID channelId, String name, ChannelType channelType);
    void remove(UUID channelId);
}
