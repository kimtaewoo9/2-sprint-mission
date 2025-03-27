package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CreatePrivateChannelRequest {

    private List<UUID> participantIds;
    private ChannelType type = ChannelType.PRIVATE;
}
