package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository {

    Optional<UserStatus> findByUser(User user);

    List<UserStatus> findByLastActiveAtAfter(Instant time);
}
