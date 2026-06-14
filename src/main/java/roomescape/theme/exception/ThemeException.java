package roomescape.theme.exception;

import roomescape.exception.RoomescapeException;

public class ThemeException extends RoomescapeException {
    public ThemeException(ThemeErrorCode errorCode) {
        super(errorCode);
    }
}
