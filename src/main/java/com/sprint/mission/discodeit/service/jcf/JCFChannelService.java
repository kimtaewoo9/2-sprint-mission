package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.repository.channel.ChannelRepository;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository) {
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
    public void modify(UUID channelId, String newName) {
        channelRepository.modify(channelId, newName);
    }

    @Override
    public Channel remove(UUID channelId) {
        return channelRepository.delete(channelId);
    }

    @Override
    public void addUser(Channel channel, User user) {
        channel.join(user);
    }

    @Override
    public void removeUser(Channel channel, User user) {
        channel.leave(user);
    }
}
