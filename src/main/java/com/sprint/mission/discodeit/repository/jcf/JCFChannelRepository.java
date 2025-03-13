package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private static final Map<UUID, Channel> channelDb = new HashMap<>();

    private static class SingletonHolder{
        private static final JCFChannelRepository INSTANCE = new JCFChannelRepository();
    }

    private JCFChannelRepository(){};

    public static JCFChannelRepository getInstance(){
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void save(Channel channel) {
        channelDb.put(channel.getId(), channel);
    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        return Optional.ofNullable(channelDb.get(channelId))
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]유효하지 않은 아이디 입니다. id : " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(channelDb.values()));
    }

    @Override
    public Channel modify(UUID channelId, String channelName) {
        Channel channel = findByChannelId(channelId);
        channel.updateName(channelName);

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        validChannelId(channelId);
        channelDb.remove(channelId);
    }

    @Override
    public void clearDb() {
        channelDb.clear();
    }

    private static void validChannelId(UUID channelId) {
        if(!channelDb.containsKey(channelId)){
            throw new NoSuchElementException("[ERROR]Channel ID Error");
        }
    }
}
