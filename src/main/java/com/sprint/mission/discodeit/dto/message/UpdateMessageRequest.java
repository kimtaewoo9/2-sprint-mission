package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateMessageRequest {

    private String content;
    private List<UUID> binaryContentIds;

    public UpdateMessageRequest(String content) {
        this.content = content;
    }
}
