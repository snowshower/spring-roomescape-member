package roomescape.reservationtime.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@Import(ReservationTimeDao.class)
public class ReservationTimeDaoTest {

    @Autowired
    private ReservationTimeDao reservationTimeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_test() {
        // given
        ReservationTime time = new ReservationTime(LocalTime.of(15, 0));

        // when
        Long id = reservationTimeDao.save(time);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void findAll_test() {
        // given
        ReservationTime time1 = new ReservationTime(LocalTime.of(15, 0));
        ReservationTime time2 = new ReservationTime(LocalTime.of(16, 0));
        reservationTimeDao.save(time1);
        reservationTimeDao.save(time2);

        // when
        List<ReservationTime> times = reservationTimeDao.findAll();

        // then
        assertAll(
                () -> assertThat(times).hasSize(2),
                () -> assertThat(times.get(0).getStartAt()).isEqualTo(LocalTime.of(15, 0)),
                () -> assertThat(times.get(1).getStartAt()).isEqualTo(LocalTime.of(16, 0))
        );
    }

    @Test
    void delete_test() {
        // given
        ReservationTime time = new ReservationTime(LocalTime.of(15, 0));
        Long id = reservationTimeDao.save(time);

        // when
        reservationTimeDao.delete(id);
        List<ReservationTime> times = reservationTimeDao.findAll();

        // then
        assertThat(times).isEmpty();
    }
}
