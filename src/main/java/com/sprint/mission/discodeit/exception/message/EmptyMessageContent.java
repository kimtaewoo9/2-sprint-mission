package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class EmptyMessageContent extends MessageException {

    public EmptyMessageContent(ErrorCode errorCode) {
        super(ErrorCode.EMPTY_MESSAGE_CONTENT);
    }

    public EmptyMessageContent(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.EMPTY_MESSAGE_CONTENT, details);
    }
}
