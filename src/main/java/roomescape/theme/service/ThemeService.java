package roomescape.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ThemeService {

    private final ThemeDao themeDao;

    public ThemeService(ThemeDao themeDao) {
        this.themeDao = themeDao;
    }

    @Transactional
    public ThemeResponse create(ThemeRequest request) {
        Theme theme = new Theme(request.name(), request.description(), request.thumbnail());
        Long id = themeDao.save(theme);
        Theme createdTheme = new Theme(id, request.name(), request.description(), request.thumbnail());

        return ThemeResponse.from(createdTheme);
    }

    public List<ThemeResponse> read() {
        List<Theme> themes = themeDao.findAll();

        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        int deletedId = themeDao.delete(id);
        if (deletedId == 0) {
            throw new ThemeException(ThemeErrorCode.THEME_NOT_FOUND);
        }
    }
}
