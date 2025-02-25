package com.sprint.mission.discodeit.service.repository.user;

import com.sprint.mission.discodeit.entity.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MapUserRepository implements UserRepository {

    private static final Map<UUID, User> userDb = new HashMap<>();
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
