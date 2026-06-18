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
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;

    public ReservationService(ReservationTimeDao reservationTimeDao, ReservationDao reservationDao, ThemeDao themeDao) {
        this.reservationTimeDao = reservationTimeDao;
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
    }


    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        ReservationTime reservationTime = reservationTimeDao.findById(request.timeId())
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_TIME_NOT_FOUND));
        Theme theme = themeDao.findById(request.themeId())
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.THEME_NOT_FOUND));

        Reservation reservation = new Reservation(request.name(), request.date(), reservationTime, theme);
        if (reservationDao.existsByDateAndTimeIdAndThemeId(request.date(), reservationTime.getId(), theme.getId())) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }

        Long id = reservationDao.save(reservation);
        Reservation createdReservation = new Reservation(id, request.name(), request.date(), reservationTime, theme);
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
