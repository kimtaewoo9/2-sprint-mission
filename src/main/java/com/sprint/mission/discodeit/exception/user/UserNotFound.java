package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNotFound extends UserException {

    public UserNotFound(ErrorCode errorCode) {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFound(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.USER_NOT_FOUND, details);
    }
}
