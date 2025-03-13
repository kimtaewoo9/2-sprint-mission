package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public FileChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(String name, ChannelType type) {
        Channel channel = new Channel(name, type);
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
    public void update(UUID channelId, String name, ChannelType channelType) {
        channelRepository.update(channelId, name, channelType);
    }

    @Override
    public void remove(UUID channelId) {
        channelRepository.delete(channelId);
    }
}
