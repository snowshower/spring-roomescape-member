package roomescape.theme.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@Import(ThemeDao.class)
public class ThemeDaoTest {

    @Autowired
    private ThemeDao themeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_test() {
        // given
        Theme theme = new Theme("테마1", "설명1", "썸네일1");

        // when
        Long id = themeDao.save(theme);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void findAll_test() {
        // given
        Theme theme1 = new Theme("테마1", "설명1", "썸네일1");
        Theme theme2 = new Theme("테마2", "설명2", "썸네일2");
        themeDao.save(theme1);
        themeDao.save(theme2);
        // when
        List<Theme> themes = themeDao.findAll();

        // then
        assertAll(
                () -> assertThat(themes).hasSize(2),
                () -> assertThat(themes).isNotNull(),
                () -> assertThat(themes.get(0).getName()).isEqualTo("테마1"),
                () -> assertThat(themes.get(1).getDescription()).isEqualTo("설명2")
        );
    }

    @Test
    void findPopularTheme_test() {
        // given
        LocalDate today = LocalDate.of(2026, 6, 16);
        Theme theme1 = new Theme("테마1", "설명1", "썸네일1");
        Theme theme2 = new Theme("테마2", "설명2", "썸네일2");
        Theme theme3 = new Theme("테마3", "설명3", "썸네일3");
        Long theme1Id = themeDao.save(theme1);
        Long theme2Id = themeDao.save(theme2);
        Long theme3Id = themeDao.save(theme3);

        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", 1L, LocalTime.of(15, 0));

        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름1", LocalDate.of(2026, 6, 12), 1L, theme1Id);
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름2", LocalDate.of(2026, 6, 13), 1L, theme1Id);
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름3", LocalDate.of(2026, 6, 14), 1L, theme1Id);
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름4", LocalDate.of(2026, 6, 13), 1L, theme2Id);
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름5", LocalDate.of(2026, 6, 14), 1L, theme2Id);
        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)",
                "이름6", LocalDate.of(2026, 6, 14), 1L, theme3Id);

        // when
        List<Theme> themes = themeDao.findPopularThemes(today.minusDays(7), today);

        // then
        assertAll(
                () -> assertThat(themes).hasSize(3),
                () -> assertThat(themes.get(0).getName()).isEqualTo("테마1"),
                () -> assertThat(themes.get(1).getName()).isEqualTo("테마2"),
                () -> assertThat(themes.get(2).getName()).isEqualTo("테마3")
        );
    }

    @Test
    void delete_test() {
        // given
        Theme theme = new Theme("테마1", "설명1", "썸네일1");
        Long id = themeDao.save(theme);

        // when
        themeDao.delete(id);
        List<Theme> themes = themeDao.findAll();

        // then
        assertThat(themes).isEmpty();
    }
}
