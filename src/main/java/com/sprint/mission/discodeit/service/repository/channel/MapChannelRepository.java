package com.sprint.mission.discodeit.service.repository.channel;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MapChannelRepository implements ChannelRepository {

    private static final Map<UUID, Channel> channelDb = new HashMap<>();
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

        // 유효 하지 않은 id를 입력 했을때 오류를 출력하고 다시 입력 받기
        return Optional.ofNullable(channelDb.get(channelId))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 아이디 입니다. id : " + channelId));

        // 입력 받은 ID에 해당하는 사용자가 없을 때,
        // 예외 처리를 repository, service 둘 중 어디서 하는 것이 자연스러운지 궁금합니다.
    }

    @Override
    public List<Channel> findAll() {
        // 목록이 없으면 빈 콜렉션 반환
        return new ArrayList<>(channelDb.values());
    }

    @Override
    public void modify(UUID channelId, String channelName) {
        Channel channel = findByChannelId(channelId);
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
