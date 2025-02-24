package com.sprint.mission.discodeit.service.repository.channel;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channelDb = new HashMap<>();
    private static final MapChannelRepository instance = new MapChannelRepository();

    private MapChannelRepository(){};

    public static MapChannelRepository getInstance(){
        return instance;
    }

    @Override
    public void save(Channel channel) {
        channelDb.put(channel.getId(), channel);
    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        return channelDb.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelDb.values());
    }

    @Override
    public void modify(UUID channelId, String channelName) {
        Channel channel = findByChannelId(channelId); // channelId가 NULL일 수 있음.
        channel.update(channelName);
    }

    @Override
    public Channel delete(UUID channelId) {
        return channelDb.remove(channelId);
    }

    @Override
    public void clearDb() {
        channelDb.clear();
    }
}
