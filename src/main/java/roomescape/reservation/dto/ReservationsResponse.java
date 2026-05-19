package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public class ReservationsResponse {

    private final List<ReservationResponse> reservationsResponse;

    private ReservationsResponse(List<ReservationResponse> reservationsDto) {
        this.reservationsResponse = reservationsDto;
    }

    public static ReservationsResponse from(List<ReservationResult> results) {
        List<ReservationResponse> responses = results.stream()
                .map(ReservationResponse::from)
                .toList();

        return new ReservationsResponse(responses);
    }

    @JsonValue
    public List<ReservationResponse> getReservationsResponse() {
        return reservationsResponse;
    }
}
