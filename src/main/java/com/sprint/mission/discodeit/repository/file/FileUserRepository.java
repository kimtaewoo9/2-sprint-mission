package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileUserRepository implements UserRepository {

    private final Path userDirectory;

    public FileUserRepository() {
        this.userDirectory = FileUtils.baseDirectory.resolve("users");
        FileUtils.initializeDirectory(userDirectory);
    }

    @Override
    public void save(User user) {
        Path userFile = userDirectory.resolve(user.getId().toString() + ".user");
        FileUtils.save(userFile, user);
    }

    @Override
    public User findByUserId(UUID userId) {
        Path userFile = userDirectory.resolve(userId.toString() + ".user");
        return Optional.ofNullable((User) FileUtils.loadById(userFile))
            .orElseThrow(
                () -> new IllegalArgumentException("[ERROR]유효 하지 않은 아이디 입니다. id : " + userId));
    }

    @Override
    public User findByUserName(String name) {
        for (User user : findAll()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        throw new IllegalArgumentException("[ERROR]유효하지 않은 이름 입니다.");
    }

    @Override
    public List<User> findAll() {
        return FileUtils.load(userDirectory);
    }

    @Override
    public void delete(UUID userId) {
        Path userFile = userDirectory.resolve(userId.toString() + ".user");
        FileUtils.delete(userFile);
    }
}
