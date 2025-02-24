package com.sprint.mission.discodeit.service.repository.user;

import com.sprint.mission.discodeit.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MapUserRepository implements UserRepository {

    private final Map<UUID, User> userDb = new HashMap<>();
    private static final MapUserRepository instance = new MapUserRepository();

    private MapUserRepository(){}

    public static UserRepository getInstance(){
        return instance;
    }

    @Override
    public void save(User user) {
        userDb.put(user.getId(), user);
    }

    @Override
    public User findByUserId(UUID userId) {
        return userDb.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userDb.values());
    }

    @Override
    public void modify(UUID userId, String name) {
        User user = userDb.get(userId);
        user.update(name);
    }

    @Override
    public User delete(UUID userId) {
        return userDb.remove(userId);
    }

    @Override
    public void cleatDb() {
        userDb.clear();
    }
}
