package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusNotFound extends UserStatusException {

    public UserStatusNotFound(ErrorCode errorCode) {
        super(ErrorCode.USER_STATUS_NOT_FOUND);
    }

    public UserStatusNotFound(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, details);
    }
}
