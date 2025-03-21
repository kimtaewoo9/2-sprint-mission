package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponseDto {

    private UUID channelId;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private ChannelType channelType;
    private Instant lastMessageTimestamp;
    private List<UUID> userIds;

    public static ChannelResponseDto from(Channel channel, Instant lastMessageTimestamp,
        List<UUID> userIds) {
        ChannelResponseDto response = new ChannelResponseDto();
        response.setChannelId(channel.getId());
        response.setName(channel.getName());
        response.setChannelType(channel.getType());
        response.setCreatedAt(channel.getCreatedAt());
        response.setUpdatedAt(channel.getUpdatedAt());
        response.setLastMessageTimestamp(lastMessageTimestamp);
        response.setUserIds(userIds);

        return response;
    }
}
