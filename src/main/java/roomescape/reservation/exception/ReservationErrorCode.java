package roomescape.reservation.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 시간입니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "존재하지 않는 예약입니다."),
    THEME_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "존재하지 않는 테마입니다."),
    ALREADY_RESERVED(HttpStatus.CONFLICT, "R004", "이미 존재하는 예약입니다."),
    RESERVATION_PAST_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "R005", "과거의 날짜에는 예약할 수 없습니다."),
    RESERVATION_PAST_TIME_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "R006", "과거의 시간에는 예약할 수 없습니다."),
    SAME_RESERVATION(HttpStatus.CONFLICT, "R007", "동일한 날짜와 시간으로는 변경할 수 없습니다.");


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
