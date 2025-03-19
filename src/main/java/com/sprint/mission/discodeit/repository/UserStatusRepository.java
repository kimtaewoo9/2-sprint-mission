package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    void save(UserStatus userStatus);

    UserStatus findByUserStatusId(UUID userStatusId);

    UserStatus findByUserId(UUID userId);

    List<UserStatus> findAll();

    void delete(UUID userStatusId);

    void deleteByUserId(UUID userId);
}
