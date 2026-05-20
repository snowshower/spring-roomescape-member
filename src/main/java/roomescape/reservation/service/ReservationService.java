package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.ReservationResult;
import roomescape.reservation.exception.*;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.model.Schedule;
import roomescape.schedule.repository.ScheduleRepository;
import roomescape.user.model.User;
import roomescape.user.service.UserService;

import java.util.List;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;

    public ReservationService(ReservationRepository reservationRepository, ScheduleRepository scheduleRepository, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.scheduleRepository = scheduleRepository;
        this.userService = userService;
    }

    @Transactional
    public ReservationResult create(Long userId, Long scheduleId) {
        User user = userService.getUserById(userId);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.SCHEDULE_NOT_FOUND));

        validateReservation(schedule);

        Reservation reservation = new Reservation(user, schedule);
        Reservation savedReservation = reservationRepository.create(reservation);

        return ReservationResult.from(savedReservation);
    }

    public List<ReservationResult> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResult::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        reservationRepository.delete(id);
    }

    public List<ReservationResult> findAllByUserId(Long id) {
        userService.getUserById(id);

        return reservationRepository.findAllByUserId(id).stream()
                .map(ReservationResult::from)
                .toList();
    }

    @Transactional
    public void cancel(Long reservationId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.isOwnedBy(currentUserId)) {
            throw new ReservationException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_CANCEL);
        }

        reservation.validateCancelOrChangeable(LocalDateTime.now());

        reservationRepository.delete(reservationId);
    }

    @Transactional
    public void changeSchedule(Long reservationId, Long newScheduleId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.isOwnedBy(currentUserId)) {
            throw new ReservationException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_CHANGE);
        }

        reservation.validateCancelOrChangeable(LocalDateTime.now());

        if (reservation.getSchedule().getId().equals(newScheduleId)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_SAME_SCHEDULE);
        }

        Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.SCHEDULE_NOT_FOUND));

        validateReservation(newSchedule);

        reservationRepository.updateSchedule(reservationId, newScheduleId);
    }

    public ReservationResult findById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        return ReservationResult.from(reservation);
    }

    private void validateReservation(Schedule schedule) {
        if (schedule.getStartAt().isBefore(LocalDateTime.now())) {
            throw new ReservationException(ReservationErrorCode.PAST_TIME_RESERVATION);
        }

        if (reservationRepository.existsByScheduleId(schedule.getId())) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }
}
