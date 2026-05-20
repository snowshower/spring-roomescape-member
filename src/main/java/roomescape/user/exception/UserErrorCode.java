package roomescape.user.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 사용자 이름입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "존재하지 않는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
