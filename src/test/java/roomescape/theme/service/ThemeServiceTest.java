package roomescape.theme.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.InvalidThemeException;
import roomescape.theme.dto.*;
import roomescape.support.DatabaseHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @Autowired
    private DatabaseHelper databaseHelper;

    @BeforeEach
    void setUp() {
        databaseHelper.cleanUp();
    }

    @Test
    void 새로운_테마를_생성하고_쩡상적으로_응답을_반환한다() {
        ThemeCreateCommand command = new ThemeCreateCommand("공포 테마", "무서워", "무서워", LocalTime.of(2, 0));
        ThemeResult result = themeService.create(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("공포 테마");
        assertThat(result.description()).isEqualTo("무서워");
        assertThat(result.imageUrl()).isEqualTo("무서워");
    }

    @Test
    void 테마를_정상적으로_삭제한다() {
        ThemeCreateCommand command = new ThemeCreateCommand("코믹 테마", "웃겨", "웃겨", LocalTime.of(2, 0));
        ThemeResult result = themeService.create(command);

        assertDoesNotThrow(() -> themeService.delete(result.id()));
    }

    @Test
    void 테마를_전체_조회한다() {
        ThemeCreateCommand command1 = new ThemeCreateCommand("테마1", "설명1", "경로1", LocalTime.of(2, 0));
        ThemeCreateCommand command2 = new ThemeCreateCommand("테마2", "설명2", "경로2", LocalTime.of(2, 0));

        ThemeResult result1 = themeService.create(command1);
        ThemeResult result2 = themeService.create(command2);

        List<ThemeResult> results = themeService.findAll();

        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
    }

    @Test
    void 최근_일주일간_예약이_많은_순서대로_인기_테마를_조회한다() {
        databaseHelper.insertUser(1L, "user1", "USER");

        ThemeCreateCommand command1 = new ThemeCreateCommand("테마1", "설명1", "경로1", LocalTime.of(2, 0));
        ThemeCreateCommand command2 = new ThemeCreateCommand("테마2", "설명2", "경로2", LocalTime.of(2, 0));

        ThemeResult result1 = themeService.create(command1);
        ThemeResult result2 = themeService.create(command2);

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(10, 0);

        databaseHelper.insertSchedule(1L, result1.id(), yesterday.toString(), yesterday.plusHours(2).toString());
        databaseHelper.insertSchedule(2L, result1.id(), yesterday.plusHours(3).toString(), yesterday.plusHours(5).toString());
        databaseHelper.insertSchedule(3L, result2.id(), yesterday.toString(), yesterday.plusHours(2).toString());

        databaseHelper.insertReservation(1L, 1L, 1L);
        databaseHelper.insertReservation(2L, 2L, 1L);
        databaseHelper.insertReservation(3L, 3L, 1L);

        List<PopularThemeResult> results = themeService.findPopularThemes(10, 7);

        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).themeName()).isEqualTo("테마1");
        assertThat(results.get(0).reservationCount()).isEqualTo(2);
        assertThat(results.get(1).themeName()).isEqualTo("테마2");
        assertThat(results.get(1).reservationCount()).isEqualTo(1);
    }

    @Test
    void 사용_중인_테마를_삭제하려고_하면_예외가_발생한다() {
        ThemeCreateCommand command = new ThemeCreateCommand("테마1", "설명1", "경로1", LocalTime.of(2, 0));
        ThemeResult result = themeService.create(command);
        databaseHelper.insertSchedule(99L, result.id(), "2026-10-10 10:00:00", "2026-10-10 12:00:00");

        assertThatThrownBy(() -> themeService.delete(result.id()))
                .isInstanceOf(InvalidThemeException.class);
    }
}
