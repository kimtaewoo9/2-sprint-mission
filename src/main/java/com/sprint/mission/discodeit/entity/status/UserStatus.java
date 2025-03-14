package com.sprint.mission.discodeit.entity.status;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class UserStatus implements Serializable {

    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;

    public UserStatus(UUID userId){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
    }
}
