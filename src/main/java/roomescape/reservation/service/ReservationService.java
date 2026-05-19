package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.AuthorizationException;
import roomescape.exception.InvalidReservationException;
import roomescape.exception.ResourceNotFoundException;
import roomescape.reservation.dto.ReservationResult;
import roomescape.reservation.model.Reservation;
import roomescape.exception.SameScheduleException;
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
                .orElseThrow(() -> new ResourceNotFoundException("등록된 스케줄이 없습니다."));

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
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약입니다."));

        if (!reservation.isOwnedBy(currentUserId)) {
            throw new AuthorizationException("예약을 취소할 권한이 없습니다.");
        }

        reservation.validateCancelOrChangeable(LocalDateTime.now());

        reservationRepository.delete(reservationId);
    }

    @Transactional
    public void changeSchedule(Long reservationId, Long newScheduleId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약입니다."));

        if (!reservation.isOwnedBy(currentUserId)) {
            throw new AuthorizationException("예약을 변경할 권한이 없습니다.");
        }

        reservation.validateCancelOrChangeable(LocalDateTime.now());

        if (reservation.getSchedule().getId().equals(newScheduleId)) {
            throw new SameScheduleException("기존과 동일한 스케줄로 변경할 수 없습니다.");
        }

        Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 스케줄입니다."));

        validateReservation(newSchedule);

        reservationRepository.updateSchedule(reservationId, newScheduleId);
    }

    public ReservationResult findById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약입니다."));

        return ReservationResult.from(reservation);
    }

    private void validateReservation(Schedule schedule) {
        if (schedule.getStartAt().isBefore(LocalDateTime.now())) {
            throw new InvalidReservationException("과거 날짜/시간의 스케줄은 예약할 수 없습니다.");
        }

        if (reservationRepository.existsByScheduleId(schedule.getId())) {
            throw new InvalidReservationException("해당 시간은 이미 예약이 완료되었습니다.");
        }
    }
}
