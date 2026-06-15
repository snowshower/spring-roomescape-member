package roomescape.reservationtime.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.time.LocalDate;
import java.time.LocalTime;
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
    void delete_test() {
        // given
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse response = reservationTimeService.create(request);
        Long id = response.getId();

        // when
        reservationTimeService.delete(id);
        List<ReservationTimeResponse> times = reservationTimeService.read();

        // then
        assertThat(times).isEmpty();
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

        // when && // then
        assertThatThrownBy(() -> reservationTimeService.delete(999L))
                .isInstanceOfSatisfying(ReservationTimeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND));
        assertThatThrownBy(() -> reservationTimeService.delete(timeId))
                .isInstanceOfSatisfying(ReservationTimeException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationTimeErrorCode.RESERVATION_TIME_ALREADY_USED));
    }
}
