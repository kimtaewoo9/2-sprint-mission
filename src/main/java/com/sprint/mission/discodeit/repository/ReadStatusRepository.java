package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.status.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    ReadStatus find(UUID readStatusId);

    List<ReadStatus> findAll();

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID readStatusId);

    void update(UUID readStatusId, UUID channelId, UUID userId);

    void delete(UUID readStatusId);

    ReadStatus isExist(UUID channelId, UUID userId);
}
