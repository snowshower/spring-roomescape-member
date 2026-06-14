package roomescape.reservationtime.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class ReservationTimeDaoTest {

    private ReservationTimeDao reservationTimeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        reservationTimeDao = new ReservationTimeDao(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE reservation IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE reservation(" +
                "id BIGINT AUTO_INCREMENT, name VARCHAR(255), `date` DATE, `time_id` BIGINT)");

        jdbcTemplate.execute("DROP TABLE reservation_time IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE reservation_time(" +
                "`id` BIGINT AUTO_INCREMENT, `start_at` TIME)");
    }

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
