package roomescape.reservation.model;

import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.schedule.exception.ScheduleErrorCode;
import roomescape.schedule.exception.ScheduleException;
import roomescape.schedule.model.Schedule;
import roomescape.user.model.User;

import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private User user;
    private Schedule schedule;

    protected Reservation() {
    }

    public Reservation(User user, Schedule schedule) {
        this(null, user, schedule);
    }

    public Reservation(Long id, User user, Schedule schedule) {
        validateUser(user);
        validateSchedule(schedule);
        this.id = id;
        this.user = user;
        this.schedule = schedule;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public void validateCancelOrChangeable(LocalDateTime currentTime) {
        LocalDateTime deadline = schedule.getStartAt().minusHours(1);
        if (!currentTime.isBefore(deadline)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_DEADLINE_PASSED);
        }
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ReservationException(ReservationErrorCode.USER_INFO_REQUIRED);
        }
    }

    private void validateSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new ReservationException(ReservationErrorCode.SCHEDULE_INFO_REQUIRED);
        }
    }
}
