package roomescape.reservation.dto;

import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

import java.time.LocalDate;

public class ReservationResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private ReservationTimeResponse reservationTime;
    private ThemeResponse theme;

    public ReservationResponse(Long id, String name, LocalDate date, ReservationTimeResponse reservationTime, ThemeResponse theme) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getName(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()));
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
        return reservationTime;
    }

    public ThemeResponse getTheme() {
        return theme;
    }
}
