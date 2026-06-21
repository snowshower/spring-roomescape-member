package roomescape.reservationtime.service;

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
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;
import roomescape.theme.service.ThemeService;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private ThemeService themeService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private Clock clock;

    @TestConfiguration
    static class TestClockConfig {
        @Primary
        @Bean
        Clock testClock() {
            return Clock.fixed(
                    LocalDateTime.of(2026, 6, 6, 14, 0, 0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant(),
                    ZoneId.systemDefault()
            );
        }
    }

    @Test
    void create_test() {
        // given
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(15, 0));

        // when
        ReservationTimeResponse response = reservationTimeService.create(request);
        Long id = response.getId();

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void read_test() {
        // given
        ReservationTimeRequest request1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeRequest request2 = new ReservationTimeRequest(LocalTime.of(16, 0));
        reservationTimeService.create(request1);
        reservationTimeService.create(request2);

        // when
        List<ReservationTimeResponse> times = reservationTimeService.read();

        // then
        assertAll(
                () -> assertThat(times).isNotNull(),
                () -> assertThat(times).hasSize(2),
                () -> assertThat(times.get(0).getStartAt()).isEqualTo(LocalTime.of(15, 0)),
                () -> assertThat(times.get(1).getStartAt()).isEqualTo(LocalTime.of(16, 0))

        );
    }

    @Test
    void readAvailableTimes_test() {
        // given
        LocalDate today = LocalDate.now(clock);
        ReservationTimeRequest request1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeRequest request2 = new ReservationTimeRequest(LocalTime.of(16, 0));
        ReservationTimeRequest request3 = new ReservationTimeRequest(LocalTime.of(17, 0));
        ReservationTimeResponse response1 = reservationTimeService.create(request1);
        ReservationTimeResponse response2 = reservationTimeService.create(request2);
        ReservationTimeResponse response3 = reservationTimeService.create(request3);
        ThemeRequest themeRequest = new ThemeRequest("테마", "설명", "썸네일");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest reservationRequest = new ReservationRequest("예약", LocalDate.of(2026, 6, 6), response1.getId(), themeResponse.getId());
        reservationService.create(reservationRequest);

        // when
        List<ReservationTimeResponse> times = reservationTimeService.readAvailableTimes(themeResponse.getId(), today);

        // then
        assertAll(
                () -> assertThat(times).isNotNull(),
                () -> assertThat(times).hasSize(3),
                () -> assertThat(times.get(0).getStartAt()).isEqualTo(response1.getStartAt()),
                () -> assertThat(times.get(0).isBooked()).isTrue(),
                () -> assertThat(times.get(1).getStartAt()).isEqualTo(response2.getStartAt()),
                () -> assertThat(times.get(1).isBooked()).isFalse(),
                () -> assertThat(times.get(2).getStartAt()).isEqualTo(response3.getStartAt()),
                () -> assertThat(times.get(2).isBooked()).isFalse()
        );
    }

    @Test
    void delete_test() {
        // given
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse response = reservationTimeService.create(request);
        Long id = response.getId();

        // when
        reservationTimeService.delete(id);
        List<ReservationTimeResponse> reservationTimes = reservationTimeService.read();

        // then
        assertThat(reservationTimes).isEmpty();
    }

    @Test
    void readAvailableTimes_fail_test() {
        // given
        LocalDate today = LocalDate.now(clock);
        ReservationTimeRequest request1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        reservationTimeService.create(request1);

        // when && then
        assertThatThrownBy(() -> reservationTimeService.readAvailableTimes(999L, today))
                .isInstanceOfSatisfying(ThemeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ThemeErrorCode.THEME_NOT_FOUND));
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
        assertThatThrownBy(() -> reservationTimeService.delete(999L))
                .isInstanceOfSatisfying(ReservationTimeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND));
        assertThatThrownBy(() -> reservationTimeService.delete(timeId))
                .isInstanceOfSatisfying(ReservationTimeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_ALREADY_USED));
    }
}
