package roomescape.reservation.dto;

import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.dto.ReservationTimeResponse;

import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private ReservationTimeResponse time;

    public ReservationResponse(Long id, String name, LocalDate date, ReservationTimeResponse time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getTime().getId(), reservation.getTime().getStartAt()));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTimeResponse getTime() {
        return time;
    }
}
