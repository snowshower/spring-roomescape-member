package roomescape.reservation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Test
    void save_test() {
        // given
        ReservationTimeRequest timeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse timeResponse = reservationTimeService.create(timeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), timeResponse.getId());

        // when
        ReservationResponse response = reservationService.create(request);
        Long id = response.getId();

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void read_test() {
        // given
        ReservationTimeRequest timeRequest1 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeRequest timeRequest2 = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse timeResponse1 = reservationTimeService.create(timeRequest1);
        ReservationTimeResponse timeResponse2 = reservationTimeService.create(timeRequest2);

        ReservationRequest request1 = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), timeResponse1.getId());
        ReservationRequest request2 = new ReservationRequest("예약2", LocalDate.of(2026, 6, 9), timeResponse2.getId());
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
        ReservationTimeRequest timeRequest = new ReservationTimeRequest(LocalTime.of(15, 0));
        ReservationTimeResponse timeResponse = reservationTimeService.create(timeRequest);

        ReservationRequest request = new ReservationRequest("예약1", LocalDate.of(2026, 6, 8), timeResponse.getId());
        ReservationResponse response = reservationService.create(request);
        Long id = response.getId();

        // when
        reservationService.delete(id);
        List<ReservationResponse> responses = reservationService.read();

        // then
        assertThat(responses).isEmpty();
    }
}
