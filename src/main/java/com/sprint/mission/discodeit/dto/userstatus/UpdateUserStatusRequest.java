package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserStatusRequest {

    private UUID userId;
}
