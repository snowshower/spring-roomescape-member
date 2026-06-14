package roomescape.reservationtime.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

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
}
