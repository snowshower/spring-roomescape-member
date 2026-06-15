package roomescape.reservation.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@Import(ReservationDao.class)
public class ReservationDaoTest {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_test() {
        // given
        ReservationTime time = new ReservationTime(1L, LocalTime.of(15, 0));
        Theme theme = new Theme(1L, "테마1", "설명1", "썸네일1");
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", time.getId(), time.getStartAt());
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());

        Reservation reservation = new Reservation("예약1", LocalDate.of(2026, 6, 8), time, theme);

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
        Theme theme1 = new Theme(2L, "테마2", "설명2", "썸네일2");
        Theme theme2 = new Theme(3L, "테마3", "설명3", "썸네일3");
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme1.getId(), theme1.getName(), theme1.getDescription(), theme1.getThumbnail());
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme2.getId(), theme2.getName(), theme2.getDescription(), theme2.getThumbnail());

        Reservation reservation1 = new Reservation("예약1", LocalDate.of(2026, 6, 8), time1, theme1);
        Reservation reservation2 = new Reservation("예약2", LocalDate.of(2026, 6, 9), time2, theme2);
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
        Theme theme = new Theme(4L, "테마1", "설명1", "썸네일1");
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", time.getId(), time.getStartAt());
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());

        Reservation reservation = new Reservation("예약1", LocalDate.of(2026, 6, 8), time, theme);
        Long id = reservationDao.save(reservation);

        // when
        reservationDao.delete(id);
        List<Reservation> reservations = reservationDao.findAll();

        // then
        assertThat(reservations).isEmpty();
    }
}
