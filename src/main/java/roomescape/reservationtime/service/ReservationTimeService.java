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

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationTimeService {

    private final ReservationTimeDao reservationTimeDao;
    private final ReservationDao reservationDao;

    public ReservationTimeService(ReservationTimeDao reservationTimeDao, ReservationDao reservationDao) {
        this.reservationTimeDao = reservationTimeDao;
        this.reservationDao = reservationDao;
    }

    @Transactional
    public ReservationTimeResponse create(ReservationTimeRequest request) {
        ReservationTime time = new ReservationTime(request.startAt());
        Long id = reservationTimeDao.save(time);
        ReservationTime createdTime = new ReservationTime(id, request.startAt());
        return ReservationTimeResponse.from(createdTime);
    }

    public List<ReservationTimeResponse> read() {
        List<ReservationTime> times = reservationTimeDao.findAll();

        return times.stream()
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
