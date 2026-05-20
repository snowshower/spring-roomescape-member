package roomescape.schedule.exception;

import roomescape.exception.RoomEscapeException;

public class ScheduleException extends RoomEscapeException {
    public ScheduleException(ScheduleErrorCode errorCode) {
        super(errorCode);
    }
}
