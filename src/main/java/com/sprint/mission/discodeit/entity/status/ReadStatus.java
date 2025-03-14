package com.sprint.mission.discodeit.entity.status;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus {

    private UUID id;
    private Instant createAt;
    private Instant updatedAt;
    private UUID channelId;
    private UUID userId;

    public ReadStatus(UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.createAt = Instant.now();
        this.updatedAt = createAt;
        this.channelId = channelId;
        this.userId = userId;
    }
}
