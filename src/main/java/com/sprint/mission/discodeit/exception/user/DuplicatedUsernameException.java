package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicatedUsernameException extends DiscodeitException {

    public DuplicatedUsernameException(ErrorCode errorCode) {
        super(ErrorCode.DUPLICATED_USERNAME);
    }

    public DuplicatedUsernameException(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.DUPLICATED_USERNAME, details);
    }
}
