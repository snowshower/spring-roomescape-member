package roomescape.user.exception;

import roomescape.exception.RoomEscapeException;

public class UserException extends RoomEscapeException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
