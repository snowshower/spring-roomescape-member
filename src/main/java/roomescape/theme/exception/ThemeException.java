package roomescape.theme.exception;

import roomescape.exception.RoomEscapeException;

public class ThemeException extends RoomEscapeException {
    public ThemeException(ThemeErrorCode errorCode) {
        super(errorCode);
    }
}
