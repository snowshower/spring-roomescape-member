package roomescape.reservationtime.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationTimeService {

    private final ReservationTimeDao reservationTimeDao;
    private final ReservationDao reservationDao;
    private final ThemeDao themeDao;

    public ReservationTimeService(ReservationTimeDao reservationTimeDao, ReservationDao reservationDao, ThemeDao themeDao) {
        this.reservationTimeDao = reservationTimeDao;
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
    }

    @Transactional
    public ReservationTimeResponse create(ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        Long id = reservationTimeDao.save(reservationTime);
        ReservationTime createdTime = new ReservationTime(id, request.startAt(), false);
        return ReservationTimeResponse.from(createdTime);
    }

    public List<ReservationTimeResponse> read() {
        List<ReservationTime> reservationTimes = reservationTimeDao.findAll();

        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public List<ReservationTimeResponse> readAvailableTimes(Long themeId, LocalDate date) {
        if (!themeDao.existsById(themeId)) {
            throw new ThemeException(ThemeErrorCode.THEME_NOT_FOUND);
        }

        return reservationTimeDao.findAvailableTimes(themeId, date).stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (reservationDao.existsByTimeId(id)) {
            throw new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_ALREADY_USED);
        }

        int deletedTimeCount = reservationTimeDao.delete(id);

        if (deletedTimeCount == 0) {
            throw new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND);
        }
    }
}
