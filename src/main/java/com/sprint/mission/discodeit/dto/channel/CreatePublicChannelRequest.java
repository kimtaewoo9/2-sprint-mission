package com.sprint.mission.discodeit.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePublicChannelRequest extends CreateChannelRequest {

    private String channelName;
}
