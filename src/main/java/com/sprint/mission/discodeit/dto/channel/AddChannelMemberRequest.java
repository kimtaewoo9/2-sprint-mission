package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddChannelMemberRequest {

    private UUID userId;
    private UUID channelId;
}
