package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path userStatusDirectory;

    public FileUserStatusRepository() {
        this.userStatusDirectory = FileUtils.baseDirectory.resolve("users");
        FileUtils.init(userStatusDirectory);
    }

    @Override
    public void save(UserStatus userStatus) {
        Path userStatusFile = userStatusDirectory.resolve(userStatus.getId().toString() + ".user");
        FileUtils.save(userStatusFile, userStatus);
    }

    @Override
    public UserStatus findByUserStatusId(UUID userStatusId) {
        Path userFile = userStatusDirectory.resolve(userStatusId.toString() + ".user");
        return Optional.ofNullable((UserStatus) FileUtils.loadById(userFile))
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "[ERROR]유효 하지 않은 아이디 입니다. id : " + userStatusId));
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return findAll().stream()
            .filter(us -> us.getUserId().equals(userId)).findAny()
            .orElse(null);
    }

    @Override
    public List<UserStatus> findAll() {
        return FileUtils.load(userStatusDirectory);
    }

    @Override
    public void delete(UUID userStatusId) {
        Path userFile = userStatusDirectory.resolve(userStatusId.toString() + ".user");
        FileUtils.delete(userFile);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findAll().stream()
            .filter(id -> id.getUserId().equals(userId)).findAny()
            .ifPresent(userStatus -> delete(userStatus.getId()));
    }
}
