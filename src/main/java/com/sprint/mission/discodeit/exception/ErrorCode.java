package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 유저 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "USER_002",
        "인증 정보가 유효하지 않습니다."),
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "USER_003", "이미 사용 중인 사용자명입니다."),
    INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, "USER_004", "사용자 정보가 유효하지 않습니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "USER_005", "이미 사용 중인 이메일입니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "USER_006", "계정이 잠겨 있습니다."),

    // 권한 관련
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "AUTH_001", "해당 작업을 수행할 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "유효하지 않은 인증 토큰입니다."),

    // 파일 관련
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_001", "파일을 찾을 수 없습니다."),
    INVALID_FILE_DATA(HttpStatus.BAD_REQUEST, "FILE_002", "파일 데이터가 유효하지 않습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "FILE_003", "파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_004", "파일 크기가 허용 한도를 초과했습니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_005", "지원하지 않는 파일 형식입니다."),

    // 채널 관련
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CHANNEL_001", "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "CHANNEL_002", "비공개 채널은 수정할 수 없습니다."),
    CHANNEL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHANNEL_003", "해당 채널에 접근할 권한이 없습니다."),

    // 메시지 관련
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "메시지를 찾을 수 없습니다."),
    MESSAGE_EDIT_TIMEOUT(HttpStatus.FORBIDDEN, "MESSAGE_002", "메시지 편집 가능 시간이 초과되었습니다."),
    EMPTY_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "MESSAGE_003", "메시지 내용이 비어있습니다."),

    // 읽음 상태 관련
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "READ_001", "읽음 상태 정보를 찾을 수 없습니다."),
    USER_ALREADY_MEMBER(HttpStatus.CONFLICT, "READ_002", "사용자는 이미 해당 채널의 멤버입니다."),

    // 유저 상태 관련
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATUS_001", "사용자 상태 정보를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "STATUS_002", "사용자 상태 정보가 이미 존재합니다."),

    // 일반 오류
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_002", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_003", "지원하지 않는 HTTP 메소드입니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON_004", "지원하지 않는 미디어 타입입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COMMON_006", "서비스를 일시적으로 사용할 수 없습니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "COMMON_007", "요청 처리 시간이 초과되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.status = httpStatus;
        this.code = code;
        this.message = message;
    }
}
