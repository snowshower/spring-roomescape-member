package roomescape.reservationtime.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservationtime.domain.ReservationTime;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReservationTimeDao {

    private final JdbcTemplate jdbcTemplate;

    public ReservationTimeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(ReservationTime reservationTime) {
        String sql = """
                INSERT INTO reservation_time (start_at) VALUES (?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, new String[]{"id"});
            ps.setObject(1, reservationTime.getStartAt());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<ReservationTime> findAll() {
        String sql = """
                SELECT id, start_at FROM reservation_time
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new ReservationTime(
                    resultSet.getLong("id"),
                    resultSet.getObject("start_at", LocalTime.class)
            );
        });
    }

    public List<ReservationTime> findAvailableTimes(Long themeId, LocalDate date) {
        String sql = """
                SELECT rt.id,
                       rt.start_at,
                       r.id IS NOT NULL AS booked
                FROM reservation_time rt
                LEFT JOIN reservation r
                ON r.time_id=rt.id
                AND r.theme_id=?
                AND r.date=?
                ORDER BY rt.start_at
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new ReservationTime(
                    resultSet.getLong("id"),
                    resultSet.getObject("start_at", LocalTime.class),
                    resultSet.getBoolean("booked")
            );
        }, themeId, date);
    }

    public Optional<ReservationTime> findById(Long id) {
        String sql = """
                SELECT id, start_at FROM reservation_time WHERE id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
                return new ReservationTime(
                        resultSet.getLong("id"),
                        resultSet.getObject("start_at", LocalTime.class)
                );
            }, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int delete(Long id) {
        String sql = """
                DELETE FROM reservation_time WHERE id = ?
                """;
        return jdbcTemplate.update(sql, id);
    }
}
