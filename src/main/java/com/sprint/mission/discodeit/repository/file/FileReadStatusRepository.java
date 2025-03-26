package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path readStatusDirectory;

    public FileReadStatusRepository() {
        this.readStatusDirectory = FileUtils.baseDirectory.resolve("readStatuses");
        FileUtils.initializeDirectory(readStatusDirectory);
    }

    @Override
    public void save(ReadStatus readStatus) {
        Path readStatusFile = readStatusDirectory.resolve(
            readStatus.getId().toString() + ".readStatus");
        FileUtils.save(readStatusFile, readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        Path readStatusFile = readStatusDirectory.resolve(readStatusId.toString() + ".readStatus");
        return Optional.ofNullable((ReadStatus) FileUtils.loadById(readStatusFile))
            .orElseThrow(
                () -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다."));
    }

    @Override
    public List<ReadStatus> findAll() {
        return FileUtils.load(readStatusDirectory);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
            .filter(rs -> rs.getUserId().equals(userId)).toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID readStatusId) {
        return findAll().stream()
            .filter(rs -> rs.getId().equals(readStatusId)).toList();
    }

    @Override
    public void update(UUID readStatusId, UUID channelId, UUID userId) {
        ReadStatus readStatus = find(readStatusId);

        readStatus.updateChannelId(channelId);
        readStatus.updateUserId(userId);

        save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        Path readStatusFile = readStatusDirectory.resolve(readStatusId.toString() + ".readStatus");
        FileUtils.delete(readStatusFile);
    }

    @Override
    public ReadStatus isExist(UUID channelId, UUID userId) {
        return findAll().stream()
            .filter(rs -> rs.getChannelId().equals(channelId) && rs.getUserId().equals(userId))
            .findAny().orElse(null);
    }
}
