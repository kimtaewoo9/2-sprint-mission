package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    @Override
    public void save(Channel channel) {

    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public Channel modify(UUID channel, String channelName) {

        return null;
    }

    @Override
    public void delete(UUID channel) {

    }

    @Override
    public void clearDb() {

    }
}
