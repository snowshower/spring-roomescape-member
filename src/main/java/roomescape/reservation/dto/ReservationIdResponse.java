package roomescape.reservation.dto;

public class ReservationIdResponse {

    private final Long id;

    private ReservationIdResponse(Long id) {
        this.id = id;
    }

    public static ReservationIdResponse from(ReservationResult result) {
        return new ReservationIdResponse(result.reservationId());
    }

    public Long getId() {
        return id;
    }
}
