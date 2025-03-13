package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileUserRepository implements UserRepository {
    private final Path userDirectory;

    private FileUserRepository() {
        this.userDirectory = FileUtils.baseDirectory.resolve("users");
        FileUtils.init(userDirectory);
    }

    private static class SingletonHolder{
        private static final FileUserRepository INSTANCE = new FileUserRepository();
    }

    public static FileUserRepository getInstance(){
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void save(User user) {
        Path userFile = userDirectory.resolve(user.getId().toString() + ".user");
        FileUtils.save(userFile, user);
    }

    @Override
    public User findByUserId(UUID userId) {
        Path userFile = userDirectory.resolve(userId.toString()+".user");
        return Optional.ofNullable((User)FileUtils.loadById(userFile))
                .orElseThrow(() -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다. id : " + userId));
    }

    @Override
    public List<User> findAll() {
        return FileUtils.load(userDirectory);
    }

    @Override
    public User update(UUID userId, String name) {
        User user = findByUserId(userId);
        user.update(name);
        return user;
    }

    @Override
    public void delete(UUID userId) {
        Path userFile = userDirectory.resolve(userId.toString()+".user");
        FileUtils.delete(userFile);
    }

    @Override
    public void clearDb() {
        FileUtils.clearDirectory(userDirectory);
    }
}
