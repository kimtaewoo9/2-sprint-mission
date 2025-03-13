package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(String name, ChannelType channelType) {
        Channel channel = new Channel(name, channelType);
        channelRepository.save(channel);
    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        return channelRepository.findByChannelId(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void modify(UUID channelId, String newName) {
        channelRepository.update(channelId, newName);
    }

    @Override
    public void remove(UUID channelId) {
        channelRepository.delete(channelId);
    }
}
