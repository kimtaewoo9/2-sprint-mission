package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateReadStatusRequest {

    private Instant newLastLeadAt;
}
