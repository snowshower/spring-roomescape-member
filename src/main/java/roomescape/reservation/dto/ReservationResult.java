package roomescape.reservation.dto;

import roomescape.reservation.model.Reservation;

import java.time.LocalDateTime;

public record ReservationResult(
        Long reservationId,
        Long userId,
        String userName,
        Long themeId,
        String themeName,
        Long scheduleId,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static ReservationResult from(Reservation reservation) {
        return new ReservationResult(reservation.getId(), reservation.getUser().getId(), reservation.getUser().getName(),
                reservation.getSchedule().getTheme().getId(), reservation.getSchedule().getTheme().getName(),
                reservation.getSchedule().getId(), reservation.getSchedule().getStartAt(), reservation.getSchedule().getEndAt());
    }
}
