package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final Clock clock;

    public ReservationService(ReservationTimeDao reservationTimeDao, ReservationDao reservationDao, ThemeDao themeDao, Clock clock) {
        this.reservationTimeDao = reservationTimeDao;
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
        this.clock = clock;
    }


    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        validatePastDateTime(request.date(), request.timeId());

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
        return reservationDao.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> readByName(String name) {
        return reservationDao.findAllByName(name).stream()
                .map(ReservationResponse::from)
                .toList();

    }

    @Transactional
    public void update(Long id, ReservationUpdateRequest request) {
        Reservation reservation = reservationDao.findByIdAndName(id, request.name())
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        validatePastDateTime(request.date(), request.timeId());
        validatePastDateTime(reservation.getDate(), reservation.getTime().getId());

        if (reservationDao.isSameDateAndTime(id, request.date(), request.timeId())) {
            throw new ReservationException(ReservationErrorCode.SAME_RESERVATION);
        }

        if (reservationDao.existsByDateAndTimeIdAndThemeId(request.date(), request.timeId(), reservation.getTheme().getId())) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }

        int updatedCount = reservationDao.update(id, request.name(), request.date(), request.timeId());
        if (updatedCount == 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }

    }

    @Transactional
    public void delete(Long id) {
        int deletedId = reservationDao.delete(id);
        if (deletedId == 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteByIdAndName(Long id, String name) {
        Reservation reservation = reservationDao.findByIdAndName(id, name)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        validatePastDateTime(reservation.getDate(), reservation.getTime().getId());

        int deletedId = reservationDao.deleteByIdAndName(id, name);
        if (deletedId == 0) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    private void validatePastDateTime(LocalDate date, Long timeId) {
        LocalDate today = LocalDate.now(clock);
        if (date.isBefore(today)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_DATE_NOT_ALLOWED);
        }

        ReservationTime reservationTime = reservationTimeDao.findById(timeId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_TIME_NOT_FOUND));

        if (date.isEqual(today) && reservationTime.getStartAt().isBefore(LocalTime.now(clock))) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_PAST_TIME_NOT_ALLOWED);
        }
    }
}
