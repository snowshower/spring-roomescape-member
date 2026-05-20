package roomescape.exception;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "G001", "입력값이 유효하지 않습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "G002", "중복된 데이터이거나 유효하지 않은 요청입니다."),
    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "G003", "필수 요청 헤더가 누락되었습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "G004", "요청 데이터의 형식이 올바르지 않습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G005", "요청 파라미터 또는 헤더의 타입이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "서버 내부에서 에러가 발생했습니다. 관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    GlobalErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
