package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReadStatusRequest {

    private UUID channelId;
    private UUID userId;
}
