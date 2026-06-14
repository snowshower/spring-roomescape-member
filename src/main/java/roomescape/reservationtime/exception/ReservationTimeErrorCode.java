package roomescape.reservationtime.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ReservationTimeErrorCode implements ErrorCode {
    RESERVATION_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 시간입니다."),
    RESERVATION_TIME_ALREADY_USED(HttpStatus.CONFLICT, "T002", "사용중인 시간은 삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ReservationTimeErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
