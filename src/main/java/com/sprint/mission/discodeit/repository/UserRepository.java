package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    User findByUserId(UUID userID);
    List<User> findAll();
    void modify(UUID userId, String name);
    User delete(UUID userId);
    void cleatDb();
}
