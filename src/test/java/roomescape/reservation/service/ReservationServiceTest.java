package roomescape.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private ThemeService themeService;

    @Test
    void save_test() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), reservationTimeResponse.getId(), themeResponse.getId());

        // when
        ReservationResponse response = reservationService.create(request);
        Long id = response.getId();

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void read_test() {
        // given
        ReservationTimeRequest reservationTimeRequest1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeRequest reservationTimeRequest2 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse1 = reservationTimeService.create(reservationTimeRequest1);
        ReservationTimeResponse reservationTimeResponse2 = reservationTimeService.create(reservationTimeRequest2);
        ThemeRequest themeRequest1 = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeRequest themeRequest2 = new ThemeRequest("테마2", "설명2", "썸네일2");
        ThemeResponse themeResponse1 = themeService.create(themeRequest1);
        ThemeResponse themeResponse2 = themeService.create(themeRequest2);

        ReservationRequest request1 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), reservationTimeResponse1.getId(), themeResponse1.getId());
        ReservationRequest request2 = new ReservationRequest("예약2", LocalDate.of(2026, 6, 9), reservationTimeResponse2.getId(), themeResponse2.getId());
        reservationService.create(request1);
        reservationService.create(request2);

        // when
        List<ReservationResponse> responses = reservationService.read();

        // then
        assertAll(
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).getName()).isEqualTo("예약1"),
                () -> assertThat(responses.get(1).getName()).isEqualTo("예약2")
        );
    }

    @Test
    void delete_test() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), reservationTimeResponse.getId(), themeResponse.getId());
        ReservationResponse response = reservationService.create(request);
        Long id = response.getId();

        // when
        reservationService.delete(id);
        List<ReservationResponse> responses = reservationService.read();

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void create_fail_test() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        Long timeId = reservationTimeResponse.getId();

        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);
        Long themeId = themeResponse.getId();

        ReservationRequest request1 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 16), 999L, themeId);
        ReservationRequest request2 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 16), timeId, 999L);
        ReservationRequest request3 = new ReservationRequest("예약3", LocalDate.of(2026, 6, 18), timeId, themeId);
        ReservationRequest request4 = new ReservationRequest("예약3", LocalDate.of(2026, 6, 18), timeId, themeId);
        reservationService.create(request3);

        // when && then
        assertThatThrownBy(() -> reservationService.create(request1))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_TIME_NOT_FOUND));
        assertThatThrownBy(() -> reservationService.create(request2))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.THEME_NOT_FOUND));
        assertThatThrownBy(() -> reservationService.create(request4))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.ALREADY_RESERVED));
    }

    @Test
    void delete_fail_test() {
        // when && then
        assertThatThrownBy(() -> reservationService.delete(999L))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }
}
