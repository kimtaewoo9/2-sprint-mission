package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponseDto {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID senderId;
    private String content;
    private UUID channelId;

    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getSenderId(),
            message.getContent(),
            message.getChannelId()
        );
    }
}
