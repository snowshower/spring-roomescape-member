package roomescape.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @Autowired
    private ReservationTimeDao reservationTimeDao;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private ThemeDao themeDao;

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
    void readByName_test() {
        // given
        String name = "유저";
        ReservationTimeRequest reservationTimeRequest1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeRequest reservationTimeRequest2 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse1 = reservationTimeService.create(reservationTimeRequest1);
        ReservationTimeResponse reservationTimeResponse2 = reservationTimeService.create(reservationTimeRequest2);
        ThemeRequest themeRequest1 = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeRequest themeRequest2 = new ThemeRequest("테마2", "설명2", "썸네일2");
        ThemeResponse themeResponse1 = themeService.create(themeRequest1);
        ThemeResponse themeResponse2 = themeService.create(themeRequest2);

        ReservationRequest request1 = new ReservationRequest(name, LocalDate.of(2026, 6, 8), reservationTimeResponse1.getId(), themeResponse1.getId());
        ReservationRequest request2 = new ReservationRequest(name, LocalDate.of(2026, 6, 8), reservationTimeResponse1.getId(), themeResponse2.getId());
        ReservationRequest request3 = new ReservationRequest("예약2", LocalDate.of(2026, 6, 9), reservationTimeResponse2.getId(), themeResponse2.getId());
        reservationService.create(request1);
        reservationService.create(request2);
        reservationService.create(request3);

        // when
        List<ReservationResponse> responses = reservationService.readByName(name);

        // then
        assertAll(
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).getName()).isEqualTo(name),
                () -> assertThat(responses.get(1).getName()).isEqualTo(name),
                () -> assertThat(responses.get(0).getTheme().getId()).isEqualTo(themeResponse1.getId()),
                () -> assertThat(responses.get(1).getTheme().getId()).isEqualTo(themeResponse2.getId())
        );
    }

    @Test
    void update_test() {
        // given
        ReservationTimeRequest reservationTimeRequest1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse1 = reservationTimeService.create(reservationTimeRequest1);
        ReservationTimeRequest reservationTimeRequest2 = new ReservationTimeRequest(LocalTime.of(16, 0));
        ReservationTimeResponse reservationTimeResponse2 = reservationTimeService.create(reservationTimeRequest2);
        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), reservationTimeResponse1.getId(), themeResponse.getId());
        ReservationResponse response = reservationService.create(request);
        Long id = response.getId();
        ReservationUpdateRequest updateRequest = new ReservationUpdateRequest("예약1", LocalDate.of(2026, 6, 9), reservationTimeResponse2.getId());

        // when
        reservationService.update(id, updateRequest);
        Reservation reservation = reservationDao.findByIdAndName(id, "예약1")
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // then
        assertAll(
                () -> assertThat(reservation).isNotNull(),
                () -> assertThat(reservation.getName()).isEqualTo("예약1"),
                () -> assertThat(reservation.getDate()).isEqualTo(LocalDate.of(2026, 6, 9)),
                () -> assertThat(reservation.getTime().getId()).isEqualTo(reservationTimeResponse2.getId()),
                () -> assertThat(reservation.getTheme().getId()).isEqualTo(themeResponse.getId())
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
    void deleteByIdAndName_test() {
        // given
        String name = "유저";
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);
        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest request1 = new ReservationRequest(name, LocalDate.of(2026, 6, 8), reservationTimeResponse.getId(), themeResponse.getId());
        ReservationRequest request2 = new ReservationRequest(name, LocalDate.of(2026, 6, 9), reservationTimeResponse.getId(), themeResponse.getId());
        ReservationResponse response1 = reservationService.create(request1);
        ReservationResponse response2 = reservationService.create(request2);
        Long id1 = response1.getId();
        Long id2 = response2.getId();

        // when
        reservationService.deleteByIdAndName(id1, name);
        List<ReservationResponse> responses = reservationService.readByName(name);

        // then
        assertAll(
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(responses.get(0).getName()).isEqualTo(name),
                () -> assertThat(responses.get(0).getTheme().getId()).isEqualTo(themeResponse.getId())
        );
    }

    @Test
    void create_fail_test() {
        // given
        ReservationTimeRequest reservationTimeRequest1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse1 = reservationTimeService.create(reservationTimeRequest1);
        ReservationTimeRequest reservationTimeRequest2 = new ReservationTimeRequest(LocalTime.of(10, 0));
        ReservationTimeResponse reservationTimeResponse2 = reservationTimeService.create(reservationTimeRequest2);
        Long timeId1 = reservationTimeResponse1.getId();
        Long timeId2 = reservationTimeResponse2.getId();

        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);
        Long themeId = themeResponse.getId();

        ReservationRequest request1 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), 999L, themeId);
        ReservationRequest request2 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), timeId1, 999L);
        ReservationRequest request3 = new ReservationRequest("예약3", LocalDate.of(2026, 6, 9), timeId1, themeId);
        ReservationRequest request4 = new ReservationRequest("예약4", LocalDate.of(2026, 6, 9), timeId1, themeId);
        reservationService.create(request3);

        ReservationRequest request5 = new ReservationRequest("예약5", LocalDate.of(2026, 6, 4), timeId1, themeId);
        ReservationRequest request6 = new ReservationRequest("예약6", LocalDate.of(2026, 6, 6), timeId2, themeId);

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
        assertThatThrownBy(() -> reservationService.create(request5))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_DATE_NOT_ALLOWED));
        assertThatThrownBy(() -> reservationService.create(request6))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_TIME_NOT_ALLOWED));
    }

    @Test
    void update_fail_test() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(reservationTimeRequest);

        ReservationTimeRequest pastReservationTimeRequest = new ReservationTimeRequest(LocalTime.of(12, 0));
        ReservationTimeResponse pastReservationTimeResponse = reservationTimeService.create(pastReservationTimeRequest);

        ReservationTimeRequest usedReservationTimeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse usedReservationTimeResponse = reservationTimeService.create(usedReservationTimeRequest);

        ThemeRequest themeRequest = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse themeResponse = themeService.create(themeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 6), reservationTimeResponse.getId(), themeResponse.getId());
        ReservationResponse response = reservationService.create(request);

        ReservationRequest usedRequest = new ReservationRequest("예약2", LocalDate.of(2026, 6, 10), usedReservationTimeResponse.getId(), themeResponse.getId());
        reservationService.create(usedRequest);
        Long id = response.getId();

        ReservationUpdateRequest sameUpdateRequest = new ReservationUpdateRequest("예약1", LocalDate.of(2026, 6, 6), reservationTimeResponse.getId());
        ReservationUpdateRequest pastDateUpdateRequest = new ReservationUpdateRequest("예약1", LocalDate.of(2026, 6, 4), reservationTimeResponse.getId());
        ReservationUpdateRequest pastTimeUpdateRequest = new ReservationUpdateRequest("예약1", LocalDate.of(2026, 6, 6), pastReservationTimeResponse.getId());
        ReservationUpdateRequest usedRequestForTest = new ReservationUpdateRequest("예약1", LocalDate.of(2026, 6, 10), usedReservationTimeResponse.getId());
        ReservationUpdateRequest invalidUpdateRequest = new ReservationUpdateRequest("이상해", LocalDate.of(2026, 6, 9), usedReservationTimeResponse.getId());

        // when && then
        assertThatThrownBy(() -> reservationService.update(id, sameUpdateRequest))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.SAME_RESERVATION));
        assertThatThrownBy(() -> reservationService.update(id, pastDateUpdateRequest))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_DATE_NOT_ALLOWED));
        assertThatThrownBy(() -> reservationService.update(id, pastTimeUpdateRequest))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_TIME_NOT_ALLOWED));
        assertThatThrownBy(() -> reservationService.update(id, usedRequestForTest))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.ALREADY_RESERVED));
        assertThatThrownBy(() -> reservationService.update(id, invalidUpdateRequest))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    @Test
    void delete_fail_test() {
        // when && then
        assertThatThrownBy(() -> reservationService.delete(999L))
                .isInstanceOfSatisfying(ReservationException.class, e ->
                        assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    @Test
    void deleteByIdAndName_fail_test() {
        // given
        Long timeId = reservationTimeDao.save(new ReservationTime(LocalTime.of(15, 0)));
        ReservationTime reservationTime = new ReservationTime(timeId, LocalTime.of(15, 0));
        Long pastTimeId = reservationTimeDao.save(new ReservationTime(LocalTime.of(10, 0)));
        ReservationTime pastReservationTime = new ReservationTime(pastTimeId, LocalTime.of(10, 0));
        Long themeId = themeDao.save(new Theme("테마", "설명", "썸네일"));
        Theme theme = new Theme(themeId, "테마", "설명", "썸네일");

        String pastDateName = "예약1";
        String pastTimeName = "예약2";

        Long pastDateReservationId = reservationDao.save(new Reservation(pastDateName, LocalDate.of(2026, 6, 4), reservationTime, theme));
        Long pastTimeReservationId = reservationDao.save(new Reservation(pastTimeName, LocalDate.of(2026, 6, 6), pastReservationTime, theme));

        // when && then
        assertAll(
                () -> assertThatThrownBy(() -> reservationService.deleteByIdAndName(pastDateReservationId, pastDateName))
                        .isInstanceOfSatisfying(ReservationException.class, e ->
                                assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_DATE_NOT_ALLOWED)),
                () -> assertThatThrownBy(() -> reservationService.deleteByIdAndName(pastTimeReservationId, pastTimeName))
                        .isInstanceOfSatisfying(ReservationException.class, e ->
                                assertThat(e.getErrorCode()).isEqualTo(ReservationErrorCode.RESERVATION_PAST_TIME_NOT_ALLOWED))
        );
    }
}
