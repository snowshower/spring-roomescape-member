package roomescape.exception;

import org.springframework.http.HttpStatus;

public class InvalidThemeException extends RoomEscapeException {
    public InvalidThemeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
