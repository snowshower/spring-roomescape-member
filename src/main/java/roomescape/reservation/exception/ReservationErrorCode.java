package roomescape.reservation.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_TIME_NOT_EXISTS(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 시간입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "존재하지 않는 예약입니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ReservationErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
