package roomescape.theme.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ThemeServiceTest {

    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private ThemeDao themeDao;
    @Autowired
    private ReservationTimeDao reservationTimeDao;
    @Autowired
    private ThemeService themeService;

    @TestConfiguration
    static class TestClockConfig {
        @Primary
        @Bean
        Clock testClock() {
            return Clock.fixed(
                    LocalDate.of(2026, 6, 18)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant(),
                    ZoneId.systemDefault()
            );
        }
    }

    @Test
    void create_test() {
        // given
        ThemeRequest request = new ThemeRequest("테마1", "설명1", "썸네일1");

        // when
        ThemeResponse response = themeService.create(request);
        Long id = response.getId();

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void read_test() {
        // given
        ThemeRequest request1 = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeRequest request2 = new ThemeRequest("테마2", "설명2", "썸네일2");

        themeService.create(request1);
        themeService.create(request2);

        // when
        List<ThemeResponse> responses = themeService.read();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses.get(0).getName()).isEqualTo("테마1"),
                () -> assertThat(responses.get(1).getDescription()).isEqualTo("설명2")
        );
    }

    @Test
    void readPopularThemes_test() {
        // given
        Theme theme1 = new Theme("테마1", "설명1", "썸네일1");
        Theme theme2 = new Theme("테마2", "설명2", "썸네일2");
        Theme theme3 = new Theme("테마3", "설명3", "썸네일3");
        Long themeId1=themeDao.save(theme1);
        Long themeId2=themeDao.save(theme2);
        Long themeId3=themeDao.save(theme3);
        Theme savedTheme1 = new Theme(themeId1, "테마1", "설명1", "썸네일1");
        Theme savedTheme2 = new Theme(themeId2, "테마2", "설명2", "썸네일2");
        Theme savedTheme3 = new Theme(themeId3, "테마3", "설명3", "썸네일3");

        ReservationTime reservationTime = new ReservationTime(LocalTime.of(15, 0));
        Long reservationTimeId=reservationTimeDao.save(reservationTime);
        ReservationTime savedReservationTime = new ReservationTime(reservationTimeId, LocalTime.of(15, 0));

        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 12), savedReservationTime, savedTheme1));
        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 13), savedReservationTime, savedTheme1));
        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 14), savedReservationTime, savedTheme1));
        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 13), savedReservationTime, savedTheme2));
        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 14), savedReservationTime, savedTheme2));
        reservationDao.save(new Reservation("이름1", LocalDate.of(2026, 6, 14), savedReservationTime, savedTheme3));

        // when
        List<ThemeResponse> themes = themeService.readPopularThemes();

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
        ThemeRequest request = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse response = themeService.create(request);
        Long id = response.getId();

        // when
        themeService.delete(id);
        List<ThemeResponse> responses = themeService.read();

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void delete_fail_test() {
        // given
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(15, 0));
        Long reservationTimeId = reservationTimeDao.save(reservationTime);
        ReservationTime savedReservationTime = new ReservationTime(reservationTimeId, LocalTime.of(15, 0));
        Theme theme = new Theme("테마1", "설명1", "썸네일1");
        Long themeId = themeDao.save(theme);
        Theme savedTheme = new Theme(themeId, "테마1", "설명1", "썸네일1");
        reservationDao.save(new Reservation("예약1", LocalDate.of(2026, 6, 16), savedReservationTime, savedTheme));

        // when && then
        assertThatThrownBy(() -> themeService.delete(999L))
                .isInstanceOfSatisfying(ThemeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ThemeErrorCode.THEME_NOT_FOUND));
        assertThatThrownBy(() -> themeService.delete(themeId))
                .isInstanceOfSatisfying(ThemeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ThemeErrorCode.THEME_ALREADY_USED));
    }
}
