package roomescape.schedule.dto;

import roomescape.schedule.model.Schedule;

import java.time.LocalDateTime;

public record ScheduleResult(
        Long id,
        String themeName,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static ScheduleResult from(Schedule schedule) {
        return new ScheduleResult(
                schedule.getId(), schedule.getTheme().getName(), schedule.getStartAt(), schedule.getEndAt()
        );
    }
}
