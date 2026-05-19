package roomescape.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.InvalidThemeException;
import roomescape.schedule.repository.ScheduleRepository;
import roomescape.theme.dto.*;
import roomescape.theme.model.Theme;
import roomescape.theme.repository.ThemeRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ScheduleRepository scheduleRepository;

    public ThemeService(ThemeRepository themeRepository, ScheduleRepository scheduleRepository) {
        this.themeRepository = themeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<ThemeResult> findAll() {
        return themeRepository.findAll().stream()
                .map(ThemeResult::from)
                .toList();
    }

    @Transactional
    public ThemeResult create(ThemeCreateCommand command) {
        Theme theme = new Theme(command.name(), command.description(), command.imageUrl(), command.requiredTime());
        Theme savedTheme = themeRepository.create(theme);

        return ThemeResult.from(savedTheme);
    }

    @Transactional
    public void delete(Long id) {
        if (scheduleRepository.existsByThemeId(id)) {
            throw new InvalidThemeException("사용 중인 테마는 삭제할 수 없습니다.");
        }
        themeRepository.delete(id);
    }

    public List<PopularThemeResult> findPopularThemes(int limit, int days) {
        return themeRepository.findPopularThemes(limit, days);
    }
}
