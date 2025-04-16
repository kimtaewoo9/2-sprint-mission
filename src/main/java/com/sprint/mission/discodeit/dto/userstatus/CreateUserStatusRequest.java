package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record CreateUserStatusRequest(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
