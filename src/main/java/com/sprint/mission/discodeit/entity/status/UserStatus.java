package com.sprint.mission.discodeit.entity.status;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus implements Serializable {

    private final UUID id;
    private final Instant createdAt;
    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public void updateUserId(UUID userId) {
        this.userId = userId;
    }

    // 마지막 접속 시간 기준으로 현재 로그한 유저 판단
    // 접속 시간 정의 :
    public boolean isOnline() {

        return false;
    }
}
