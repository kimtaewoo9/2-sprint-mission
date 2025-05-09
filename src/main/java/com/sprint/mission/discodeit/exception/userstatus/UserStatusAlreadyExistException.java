package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusAlreadyExistException extends UserStatusException {

    public UserStatusAlreadyExistException(ErrorCode errorCode) {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS);
    }

    public UserStatusAlreadyExistException(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_ALREADY_EXISTS, details);
    }
}
