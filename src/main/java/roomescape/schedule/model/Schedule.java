package roomescape.schedule.model;

import roomescape.schedule.exception.*;
import roomescape.theme.model.Theme;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Schedule {

    private static final LocalTime OPENING_TIME = LocalTime.of(10, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(20, 0);

    private Long id;
    private Theme theme;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public Schedule() {
    }

    public Schedule(LocalDateTime startAt, Theme theme) {
        this(null, startAt, theme);
    }

    public Schedule(Long id, LocalDateTime startAt, Theme theme) {
        validateStartAt(id, startAt);
        validateTheme(theme);
        this.id = id;
        this.startAt = startAt;
        this.endAt = calculateEndAt(id, startAt, theme);
        this.theme = theme;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public Theme getTheme() {
        return theme;
    }

    private LocalDateTime calculateEndAt(Long id, LocalDateTime startAt, Theme theme) {
        LocalTime requiredTime = theme.getRequiredTime();

        LocalDateTime endAt = startAt.plusHours(requiredTime.getHour())
                .plusMinutes(requiredTime.getMinute());

        validateEndAt(id, endAt);
        return endAt;
    }

    private void validateStartAt(Long id, LocalDateTime startAt) {
        if (startAt == null) {
            throw new ScheduleException(ScheduleErrorCode.SCHEDULE_START_TIME_REQUIRED);
        }

        if (id == null && startAt.isBefore(LocalDateTime.now())) {
            throw new ScheduleException(ScheduleErrorCode.PAST_TIME_SCHEDULE_CREATION);
        }

        if (id != null) return;

        LocalTime startTime = startAt.toLocalTime();

        if (startTime.isBefore(OPENING_TIME)) {
            throw new ScheduleException(ScheduleErrorCode.SCHEDULE_START_TIME_TOO_EARLY);
        }
    }

    private void validateEndAt(Long id, LocalDateTime endAt) {
        if (endAt == null) {
            throw new ScheduleException(ScheduleErrorCode.SCHEDULE_END_TIME_REQUIRED);
        }

        if (id != null) return;

        LocalTime endTime = endAt.toLocalTime();

        if (endTime.isAfter(CLOSE_TIME)) {
            throw new ScheduleException(ScheduleErrorCode.SCHEDULE_END_TIME_TOO_LATE);
        }
    }

    private void validateTheme(Theme theme) {
        if (theme == null) {
            throw new ScheduleException(ScheduleErrorCode.THEME_INFO_REQUIRED);
        }
    }
}
