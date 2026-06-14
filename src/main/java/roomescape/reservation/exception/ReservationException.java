package roomescape.reservation.exception;

import roomescape.exception.RoomescapeException;

public class ReservationException extends RoomescapeException {
    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode);
    }
}
