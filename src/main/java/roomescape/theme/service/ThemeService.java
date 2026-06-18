package roomescape.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ThemeService {

    private final Clock clock;
    private final ThemeDao themeDao;
    private final ReservationDao reservationDao;

    public ThemeService(Clock clock, ThemeDao themeDao, ReservationDao reservationDao) {
        this.clock = clock;
        this.themeDao = themeDao;
        this.reservationDao = reservationDao;
    }

    @Transactional
    public ThemeResponse create(ThemeRequest request) {
        Theme theme = new Theme(request.name(), request.description(), request.thumbnail());
        Long id = themeDao.save(theme);
        Theme createdTheme = new Theme(id, request.name(), request.description(), request.thumbnail());

        return ThemeResponse.from(createdTheme);
    }

    public List<ThemeResponse> read() {
        return themeDao.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> readPopularThemes() {
        LocalDate today = LocalDate.now(clock);
        return themeDao.findPopularThemes(today.minusDays(7), today).stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (reservationDao.existsByThemeId(id)) {
            throw new ThemeException(ThemeErrorCode.THEME_ALREADY_USED);
        }
        int deletedId = themeDao.delete(id);
        if (deletedId == 0) {
            throw new ThemeException(ThemeErrorCode.THEME_NOT_FOUND);
        }
    }
}
