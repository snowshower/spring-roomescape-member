package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;

    public ReservationService(ReservationTimeDao reservationTimeDao, ReservationDao reservationDao) {
        this.reservationTimeDao = reservationTimeDao;
        this.reservationDao = reservationDao;
    }


    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        ReservationTime time = reservationTimeDao.findById(request.timeId())
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_TIME_NOT_EXISTS));

        Reservation reservation = new Reservation(request.name(), request.date(), time);
        Long id = reservationDao.save(reservation);
        Reservation createdReservation = new Reservation(id, request.name(), request.date(), time);
        return ReservationResponse.from(createdReservation);
    }

    public List<ReservationResponse> read() {
        List<Reservation> reservations = reservationDao.findAll();

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        int deletedId = reservationDao.delete(id);
        if (deletedId == 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }
}
