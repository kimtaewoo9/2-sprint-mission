package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UUID create(CreateUserRequest request);

    UserResponseDto findByUserId(UUID userId);

    List<UserResponseDto> findAll();

    UUID update(UUID userId, UpdateUserRequest request);

    void remove(UUID userId);
}
