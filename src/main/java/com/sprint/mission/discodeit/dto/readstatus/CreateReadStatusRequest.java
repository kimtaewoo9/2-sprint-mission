package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReadStatusRequest {

    private UUID channelId;
    private UUID userId;
}
