package roomescape.reservationtime.dto;

import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeResponse {
    private Long id;
    private LocalTime startAt;

    public ReservationTimeResponse(Long id, LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTimeResponse from(ReservationTime time) {
        return new ReservationTimeResponse(time.getId(), time.getStartAt());
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }
}
