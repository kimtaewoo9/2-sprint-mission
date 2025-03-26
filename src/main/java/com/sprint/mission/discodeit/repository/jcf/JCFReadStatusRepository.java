package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {

    Map<UUID, ReadStatus> readStatusDb = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) {
        readStatusDb.put(readStatus.getId(), readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusDb.get(readStatusId);
    }

    @Override
    public List<ReadStatus> findAll() {
        return readStatusDb.values().stream().toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusDb.values().stream()
            .filter(rs -> rs.getUserId().equals(userId))
            .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatusDb.values().stream()
            .filter(rs -> rs.getChannelId().equals(channelId))
            .toList();
    }

    @Override
    public void update(UUID readStatusId, UUID channelId, UUID userId) {
        ReadStatus readStatus = find(readStatusId);
        readStatus.updateChannelId(channelId);
        readStatus.updateUserId(userId);

        readStatusDb.put(readStatus.getId(), readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = find(readStatusId);

        readStatusDb.remove(readStatus.getId());
    }

    @Override
    public ReadStatus isExist(UUID channelId, UUID userId) {
        return findAll().stream()
            .filter(rs -> rs.getChannelId().equals(channelId) && rs.getUserId().equals(userId))
            .findAny().orElse(null);
    }
}
