package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(String name) {
        Channel channel = new Channel(name);
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
    public void modify(UUID channelId, String name) {
        channelRepository.modify(channelId, name);
    }

    @Override
    public void remove(UUID channelId) {
        channelRepository.delete(channelId);
    }

    @Override
    public void addUser(Channel channel, User user) {

    }

    @Override
    public void removeUser(Channel channel, User user) {

    }

    @Override
    public void addMessage(Channel channel, Message message) {

    }
}
