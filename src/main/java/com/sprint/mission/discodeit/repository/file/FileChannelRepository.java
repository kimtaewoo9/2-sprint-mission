package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class FileChannelRepository implements ChannelRepository {
    private final Path channelDirectory;

    public FileChannelRepository() {
        this.channelDirectory = FileUtils.getBaseDirectory().resolve("channels");
        FileUtils.init(channelDirectory);
    }

    @Override
    public void save(Channel channel) {
        Path channelFile = channelDirectory.resolve(channel.getId().toString() + ".channel");
        FileUtils.save(channelFile, channel);
    }

    @Override
    public Channel findByChannelId(UUID channelId) {
        Path channelFile = channelDirectory.resolve(channelId.toString() + ".channel");
        return Optional.ofNullable((Channel)FileUtils.loadById(channelFile))
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다. id : " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        return FileUtils.load(channelDirectory);
    }

    @Override
    public Channel update(UUID channelId, String channelName, ChannelType channelType) {
        Channel channel = findByChannelId(channelId);
        channel.updateName(channelName);
        channel.updateChannelType(channelType);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Path channelFile = channelDirectory.resolve(channelId.toString()+".channel");
        FileUtils.delete(channelFile);
    }

    @Override
    public void clearDb() {
        FileUtils.clearDirectory(channelDirectory);
    }
}
