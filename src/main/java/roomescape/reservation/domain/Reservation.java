package roomescape.reservation.domain;

import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;

public class Reservation {
    private Long id;
    private String name;
    private LocalDate date;
    private ReservationTime reservationTime;
    private Theme theme;

    public Reservation() {
    }

    public Reservation(String name, LocalDate date, ReservationTime reservationTime, Theme theme) {
        this(null, name, date, reservationTime, theme);
    }

    public Reservation(Long id, String name, LocalDate date, ReservationTime reservationTime, Theme theme) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
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

    public ReservationTime getTime() {
        return reservationTime;
    }

    public Theme getTheme() {
        return theme;
    }
}
