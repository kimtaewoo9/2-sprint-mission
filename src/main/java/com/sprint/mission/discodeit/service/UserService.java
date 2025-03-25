package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UUID create(CreateUserRequest request);

    UUID create(CreateUserRequest request, CreateBinaryContentRequest binaryContentRequest);

    UserResponseDto findByUserId(UUID userId);

    List<UserResponseDto> findAll();

    UUID update(UUID userId, UpdateUserRequest request);

    UUID update(UUID userId, UpdateUserRequest request,
        UUID profileId);

    void remove(UUID userId);
}
