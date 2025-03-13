package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel);
    Channel findByChannelId(UUID channelId);
    List<Channel> findAll();
    Channel modify(UUID channelId, String channelName, ChannelType channelType);
    void delete(UUID channelId);
    void clearDb();
}
