package roomescape.reservation.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class ReservationDaoTest {

    private ReservationDao reservationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        reservationDao = new ReservationDao(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE reservation IF EXISTS");
        jdbcTemplate.execute("DROP TABLE reservation_time IF EXISTS");

        jdbcTemplate.execute("CREATE TABLE reservation(" +
                "id BIGINT AUTO_INCREMENT, name VARCHAR(255), `date` DATE, `time_id` BIGINT)");
        jdbcTemplate.execute("CREATE TABLE reservation_time(" +
                "`id` BIGINT AUTO_INCREMENT, `start_at` TIME)");

    }

    @Test
    void save_test() {
        // given
        ReservationTime time = new ReservationTime(1L, LocalTime.of(15, 0));
        Reservation reservation = new Reservation("예약1", LocalDate.of(2026, 6, 8), time);

        // when
        Long id = reservationDao.save(reservation);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void findAll_test() {
        // given
        ReservationTime time1 = new ReservationTime(2L, LocalTime.of(15, 0));
        ReservationTime time2 = new ReservationTime(3L, LocalTime.of(15, 0));
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", time1.getId(), time1.getStartAt());
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", time2.getId(), time2.getStartAt());
        Reservation reservation1 = new Reservation("예약1", LocalDate.of(2026, 6, 8), time1);
        Reservation reservation2 = new Reservation("예약2", LocalDate.of(2026, 6, 9), time2);
        reservationDao.save(reservation1);
        reservationDao.save(reservation2);

        // when
        List<Reservation> reservations = reservationDao.findAll();

        // then
        assertAll(
                () -> assertThat(reservations).isNotNull(),
                () -> assertThat(reservations).hasSize(2),
                () -> assertThat(reservations.get(0).getName()).isEqualTo("예약1"),
                () -> assertThat(reservations.get(1).getName()).isEqualTo("예약2")
        );
    }

    @Test
    void delete_test() {
        // given
        ReservationTime time = new ReservationTime(4L, LocalTime.of(15, 0));
        Reservation reservation = new Reservation("예약1", LocalDate.of(2026, 6, 8), time);
        Long id = reservationDao.save(reservation);

        // when
        reservationDao.delete(id);
        List<Reservation> reservations = reservationDao.findAll();

        // then
        assertThat(reservations).isEmpty();
    }
}
