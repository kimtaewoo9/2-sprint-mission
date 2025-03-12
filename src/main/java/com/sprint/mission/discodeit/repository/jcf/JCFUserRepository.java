package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private static final Map<UUID, User> userDb = new HashMap<>();

    private JCFUserRepository(){}

    private static class SingletonHolder{
        private static final JCFUserRepository INSTANCE = new JCFUserRepository();
    }

    public static JCFUserRepository getInstance(){
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void save(User user) {
        userDb.put(user.getId(), user);
    }

    @Override
    public User findByUserId(UUID userId) {
        return Optional.ofNullable(userDb.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]유효하지 않은 아이디 입니다. id : " + userId));
    }

    @Override
    public List<User> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(userDb.values()));
    }

    @Override
    public User modify(UUID userId, String newName) {
        User user = findByUserId(userId);
        user.update(newName);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        validUserId(userId);
        userDb.remove(userId);
    }

    @Override
    public void clearDb() {
        userDb.clear();
    }

    private static void validUserId(UUID userId) {
        if(!userDb.containsKey(userId)){
            throw new NoSuchElementException("[ERROR]User ID Error");
        }
    }
}
