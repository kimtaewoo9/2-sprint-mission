package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    @Override
    public void create(String name) {

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
    public void modify(UUID channelId, String name) {

    }

    @Override
    public Channel remove(UUID channelId) {
        return null;
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
