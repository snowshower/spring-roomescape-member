package roomescape.reservationtime.dto;

import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeResponse {
    private Long id;
    private LocalTime startAt;
    private boolean booked;

    public ReservationTimeResponse(Long id, LocalTime startAt, boolean booked) {
        this.id = id;
        this.startAt = startAt;
        this.booked = booked;
    }

    public static ReservationTimeResponse from(ReservationTime time) {
        return new ReservationTimeResponse(time.getId(), time.getStartAt(), time.isBooked());
    }

    public Long getId() {
        return id;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public boolean isBooked() {
        return booked;
    }
}
