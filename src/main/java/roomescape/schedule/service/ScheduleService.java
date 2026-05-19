package roomescape.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.InvalidScheduleException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.dto.ScheduleResult;
import roomescape.schedule.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ReservationRepository reservationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ScheduleResult> findAll(Long themeId, LocalDate date) {
        return scheduleRepository.findAll(themeId, date).stream()
                .map(ScheduleResult::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (reservationRepository.existsByScheduleId(id)) {
            throw new InvalidScheduleException("예약이 존재하는 스케줄은 삭제할 수 없습니다.");
        }

        scheduleRepository.delete(id);
    }
}
