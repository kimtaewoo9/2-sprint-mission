package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileUploadFail extends FileException {

    public FileUploadFail(ErrorCode errorCode) {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

    public FileUploadFail(ErrorCode errorCode, Map<String, Object> details) {
        super(ErrorCode.FILE_UPLOAD_FAILED, details);
    }
}
