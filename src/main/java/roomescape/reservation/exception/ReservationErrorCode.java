package roomescape.reservation.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {
    SCHEDULE_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "R001", "스케줄 정보는 필수입니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "R002", "해당 시간은 이미 예약이 완료되었습니다."),
    USER_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "R003", "사용자 정보는 필수입니다."),
    PAST_TIME_RESERVATION(HttpStatus.BAD_REQUEST, "R004", "과거 날짜/시간의 스케줄은 예약할 수 없습니다."),
    RESERVATION_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "R005", "방탈출 시작 1시간 전부터는 예약을 취소하거나 변경할 수 없습니다."),
    RESERVATION_SAME_SCHEDULE(HttpStatus.BAD_REQUEST, "R006", "기존과 동일한 스케줄로 변경할 수 없습니다."),
    UNAUTHORIZED_RESERVATION_CANCEL(HttpStatus.FORBIDDEN, "R007", "예약을 취소할 권한이 없습니다."),
    UNAUTHORIZED_RESERVATION_CHANGE(HttpStatus.FORBIDDEN, "R008", "예약을 변경할 권한이 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R009", "존재하지 않는 예약입니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "R010", "존재하지 않는 스케줄입니다.");

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
