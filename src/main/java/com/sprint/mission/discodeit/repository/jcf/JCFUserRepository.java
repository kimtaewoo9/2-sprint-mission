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

        // 유효 하지 않은 id를 입력 했을때 오류를 출력하고 다시 입력 받기
        return Optional.ofNullable(userDb.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 아이디 입니다. id : " + userId));
    }

    @Override
    public List<User> findAll() {
        // 목록이 없으면 빈 콜렉션 반환
        return Collections.unmodifiableList(new ArrayList<>(userDb.values()));
    }

    @Override
    public User modify(UUID userId, String newName) {
        User userNullable = userDb.get(userId);
        User user = Optional.ofNullable(userNullable).
                orElseThrow(() -> new NoSuchElementException("User ID Error"));
        user.update(newName);

        return user;
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
