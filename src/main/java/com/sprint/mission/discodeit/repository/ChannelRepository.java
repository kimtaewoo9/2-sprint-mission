package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel);
    Channel findByChannelId(UUID channelId);
    List<Channel> findAll();
    Channel modify(UUID channelId, String channelName);
    void delete(UUID channelId);
    void clearDb();
}
