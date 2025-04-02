package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    void create(CreateUserStatusRequest request);

    UserStatus findByUserStatusId(UUID id);

    List<UserStatus> findAll();

    void update(UUID userStatusId, UpdateUserStatusRequest request);

    void updateByUserId(UUID userId, UpdateUserStatusRequest request);
    
    void delete(UUID userId);
}
