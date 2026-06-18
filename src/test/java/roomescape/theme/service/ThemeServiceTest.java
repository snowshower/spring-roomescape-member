package roomescape.theme.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;
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

    @Autowired
    private ThemeService themeService;
    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private ReservationService reservationService;

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
        ThemeRequest request1 = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeRequest request2 = new ThemeRequest("테마2", "설명2", "썸네일2");
        ThemeRequest request3 = new ThemeRequest("테마3", "설명3", "썸네일3");
        ThemeResponse response1 = themeService.create(request1);
        ThemeResponse response2 = themeService.create(request2);
        ThemeResponse response3 = themeService.create(request3);
        Long theme1Id = response1.getId();
        Long theme2Id = response2.getId();
        Long theme3Id = response3.getId();

        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        Long timeId = reservationTimeResponse.getId();

        reservationService.create(new ReservationRequest("이름1", LocalDate.of(2026, 6, 12), timeId, theme1Id));
        reservationService.create(new ReservationRequest("이름2", LocalDate.of(2026, 6, 13), timeId, theme1Id));
        reservationService.create(new ReservationRequest("이름3", LocalDate.of(2026, 6, 14), timeId, theme1Id));
        reservationService.create(new ReservationRequest("이름4", LocalDate.of(2026, 6, 13), timeId, theme2Id));
        reservationService.create(new ReservationRequest("이름5", LocalDate.of(2026, 6, 14), timeId, theme2Id));
        reservationService.create(new ReservationRequest("이름6", LocalDate.of(2026, 6, 14), timeId, theme3Id));

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
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        Long timeId = reservationTimeResponse.getId();
        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);
        Long themeId = themeResponse.getId();
        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 16), timeId, themeId);
        reservationService.create(request);

        // when && then
        assertThatThrownBy(() -> themeService.delete(999L))
                .isInstanceOfSatisfying(ThemeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ThemeErrorCode.THEME_NOT_FOUND));
        assertThatThrownBy(() -> themeService.delete(themeId))
                .isInstanceOfSatisfying(ThemeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ThemeErrorCode.THEME_ALREADY_USED));
    }
}
