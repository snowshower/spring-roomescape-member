package roomescape.reservation.dto;

import java.time.LocalDateTime;

public class ReservationResponse {

    private final Long reservationId;
    private final Long userId;
    private final String userName;
    private final Long themeId;
    private final String themeName;
    private final Long scheduleId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    private ReservationResponse(Long reservationId, Long userId, String userName, Long themeId, String themeName, Long scheduleId, LocalDateTime startAt, LocalDateTime endAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.userName = userName;
        this.themeId = themeId;
        this.themeName = themeName;
        this.scheduleId = scheduleId;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static ReservationResponse from(ReservationResult result) {
        return new ReservationResponse(
                result.reservationId(),
                result.userId(),
                result.userName(),
                result.themeId(),
                result.themeName(),
                result.scheduleId(),
                result.startAt(),
                result.endAt()
        );
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getThemeId() {
        return themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }
}
