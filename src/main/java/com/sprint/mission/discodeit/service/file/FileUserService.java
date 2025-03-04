package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    @Override
    public void create(String name) {

    }

    @Override
    public User findByUserId(UUID userId) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void update(UUID userId, String userName) {

    }

    @Override
    public User remove(UUID userId) {
        return null;
    }
}
