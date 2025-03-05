package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    @Override
    public void save(User user) {

    }

    @Override
    public User findByUserId(UUID userID) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User modify(UUID userId, String name) {

        return null;
    }

    @Override
    public void delete(UUID userId) {
    }

    @Override
    public void cleatDb() {

    }
}
