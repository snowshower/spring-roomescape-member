package roomescape.exception;

import org.springframework.http.HttpStatus;

public class InvalidScheduleException extends RoomEscapeException {
    public InvalidScheduleException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
