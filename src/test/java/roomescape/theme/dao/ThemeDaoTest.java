package roomescape.theme.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.theme.domain.Theme;

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
