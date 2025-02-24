package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    void register(User user);
    User getUserById(UUID userId);
    List<User> findAll();
    void update(UUID userId, String userName);
    User remove(UUID userId);
}
