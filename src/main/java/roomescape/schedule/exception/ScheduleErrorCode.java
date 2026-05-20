package roomescape.schedule.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum ScheduleErrorCode implements ErrorCode {

    SCHEDULE_START_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "S001", "예약 시작 시간은 필수입니다."),
    SCHEDULE_END_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "S002", "예약 종료 시간은 필수입니다."),
    THEME_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "S003", "테마 정보는 필수입니다."),
    PAST_TIME_SCHEDULE_CREATION(HttpStatus.BAD_REQUEST, "S004", "과거 날짜/시간에는 스케줄을 생성할 수 없습니다."),
    SCHEDULE_ALREADY_HAS_RESERVATIONS(HttpStatus.BAD_REQUEST, "S005", "예약이 존재하는 스케줄은 삭제할 수 없습니다."),
    SCHEDULE_START_TIME_TOO_EARLY(HttpStatus.BAD_REQUEST, "S006", "오전 10시 이전에는 예약이 불가능합니다."),
    SCHEDULE_END_TIME_TOO_LATE(HttpStatus.BAD_REQUEST, "S007", "오후 8시 이후에는 예약이 불가능합니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    ScheduleErrorCode(HttpStatus httpStatus, String errorCode, String message) {
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
