package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    void create(String name);
    User findByUserId(UUID userId);
    List<User> findAll();
    void update(UUID userId, String userName);
    void remove(UUID userId);
}
