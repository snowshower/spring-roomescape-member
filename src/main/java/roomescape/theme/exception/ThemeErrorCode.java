package roomescape.theme.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ThemeErrorCode implements ErrorCode {
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 테마입니다."),
    THEME_ALREADY_USED(HttpStatus.CONFLICT, "T002", "사용중인 테마는 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ThemeErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
