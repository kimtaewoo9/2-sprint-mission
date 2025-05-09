package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicatedEmailException extends UserException {

    public DuplicatedEmailException(ErrorCode errorCode) {
        super(ErrorCode.DUPLICATED_EMAIL);
    }

    public DuplicatedEmailException(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.DUPLICATED_EMAIL, details);
    }
}
