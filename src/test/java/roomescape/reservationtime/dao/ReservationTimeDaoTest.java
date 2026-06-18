package roomescape.reservationtime.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.theme.domain.Theme;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalDate;
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
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(15, 0));

        // when
        Long id = reservationTimeDao.save(reservationTime);

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void findAll_test() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(LocalTime.of(15, 0));
        ReservationTime reservationTime2 = new ReservationTime(LocalTime.of(16, 0));
        reservationTimeDao.save(reservationTime1);
        reservationTimeDao.save(reservationTime2);

        // when
        List<ReservationTime> reservationTimes = reservationTimeDao.findAll();

        // then
        assertAll(
                () -> assertThat(reservationTimes).hasSize(2),
                () -> assertThat(reservationTimes.get(0).getStartAt()).isEqualTo(LocalTime.of(15, 0)),
                () -> assertThat(reservationTimes.get(1).getStartAt()).isEqualTo(LocalTime.of(16, 0))
        );
    }

    @Test
    void findAvailableTimes_test() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(1L, LocalTime.of(15, 0), false);
        ReservationTime reservationTime2 = new ReservationTime(2L, LocalTime.of(16, 0), false);
        Theme theme1 = new Theme(1L, "테마1", "설명1", "썸네일1");
        Theme theme2 = new Theme(2L, "테마2", "설명2", "썸네일2");

        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", reservationTime1.getId(), reservationTime1.getStartAt());
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", reservationTime2.getId(), reservationTime2.getStartAt());
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme1.getId(), theme1.getName(), theme1.getDescription(), theme1.getThumbnail());
        jdbcTemplate.update("INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)", theme2.getId(), theme2.getName(), theme2.getDescription(), theme2.getThumbnail());

        jdbcTemplate.update("INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)", "이름1", LocalDate.of(2026, 6, 20), reservationTime1.getId(), theme1.getId());

        // when
        List<ReservationTime> availableTimes1 = reservationTimeDao.findAvailableTimes(theme1.getId(), LocalDate.of(2026, 6, 20));
        List<ReservationTime> availableTimes2 = reservationTimeDao.findAvailableTimes(theme2.getId(), LocalDate.of(2026, 6, 20));

        // then
        assertAll(
                () -> assertThat(availableTimes1).hasSize(2),
                () -> assertThat(availableTimes1.get(0).getStartAt()).isEqualTo(reservationTime1.getStartAt()),
                () -> assertThat(availableTimes1.get(0).isBooked()).isTrue(),
                () -> assertThat(availableTimes1.get(1).isBooked()).isFalse(),
                () -> assertThat(availableTimes2).hasSize(2),
                () -> assertThat(availableTimes2.get(0).getStartAt()).isEqualTo(reservationTime1.getStartAt()),
                () -> assertThat(availableTimes2.get(0).isBooked()).isFalse(),
                () -> assertThat(availableTimes2.get(1).isBooked()).isFalse()
        );
    }

    @Test
    void delete_test() {
        // given
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(15, 0));
        Long id = reservationTimeDao.save(reservationTime);

        // when
        reservationTimeDao.delete(id);
        List<ReservationTime> times = reservationTimeDao.findAll();

        // then
        assertThat(times).isEmpty();
    }
}
