package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(ErrorCode errorCode) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED);
    }

    public PrivateChannelUpdateException(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED, details);
    }
}
