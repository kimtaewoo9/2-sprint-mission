package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserStatusRequest {

    private UUID userId;
}
