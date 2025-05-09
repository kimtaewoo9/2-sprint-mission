package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageNotFound extends MessageException {

    public MessageNotFound(ErrorCode errorCode) {
        super(ErrorCode.MESSAGE_NOT_FOUND);
    }

    public MessageNotFound(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.MESSAGE_NOT_FOUND, details);
    }
}
