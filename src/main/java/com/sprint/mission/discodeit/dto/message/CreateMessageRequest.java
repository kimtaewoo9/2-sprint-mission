package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {

    private String content;
    private UUID channelId;
    private UUID senderId;
}
