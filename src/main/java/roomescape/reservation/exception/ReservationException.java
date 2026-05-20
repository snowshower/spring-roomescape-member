package roomescape.reservation.exception;

import roomescape.exception.RoomEscapeException;

public class ReservationException extends RoomEscapeException {
    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode);
    }
}
