package roomescape.theme.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ThemeServiceTest {

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
        ReservationTimeRequest timeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse timeResponse = reservationTimeService.create(timeRequest);
        Long timeId = timeResponse.getId();
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
