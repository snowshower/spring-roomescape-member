package roomescape.reservationtime.domain;

import java.time.LocalTime;

public class ReservationTime {
    private Long id;
    private LocalTime startAt;
    private boolean booked;

    public ReservationTime() {
    }

    public ReservationTime(Long id, LocalTime startAt) {
        this(id, startAt, false);
    }

    public ReservationTime(LocalTime startAt) {
        this(null, startAt, false);
    }

    public ReservationTime(Long id, LocalTime startAt, boolean booked) {
        this.id = id;
        this.startAt = startAt;
        this.booked = booked;
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
