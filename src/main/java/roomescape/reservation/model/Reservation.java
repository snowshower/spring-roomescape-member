package roomescape.reservation.model;

import roomescape.exception.InvalidReservationException;
import roomescape.exception.ReservationDeadlineException;
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
            throw new ReservationDeadlineException("방탈출 시작 1시간 전부터는 예약을 취소하거나 변경할 수 없습니다.");
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
            throw new InvalidReservationException("사용자 정보는 필수입니다.");
        }
    }

    private void validateSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new InvalidReservationException("스케줄 정보는 필수입니다.");
        }
    }
}
