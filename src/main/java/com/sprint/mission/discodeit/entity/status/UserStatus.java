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
    private Instant lastSeenAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.lastSeenAt = createdAt;
    }

    public void updateUserId(UUID userId) {
        this.userId = userId;
    }

    // 마지막 업데이트 시간 기준으로 현재 로그한 유저 판단
    public boolean isOnline() {
        return Instant.now().minusSeconds(5 * 60).isBefore(lastSeenAt);
    }

    public void updateUpdatedAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
