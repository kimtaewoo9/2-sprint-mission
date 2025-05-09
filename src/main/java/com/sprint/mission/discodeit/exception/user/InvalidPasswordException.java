package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidPasswordException extends UserException {

    public InvalidPasswordException(ErrorCode errorCode) {
        super(ErrorCode.INVALID_USER_INPUT);
    }

    public InvalidPasswordException(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.INVALID_USER_INPUT, details);
    }
}
