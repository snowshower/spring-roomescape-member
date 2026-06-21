package roomescape.reservation.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReservationDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> reservationRowMapper = (resultSet, rowNum) -> {
        ReservationTime reservationTime = new ReservationTime(
                resultSet.getLong("time_id"),
                resultSet.getObject("start_at", LocalTime.class)
        );
        Theme theme = new Theme(
                resultSet.getLong("theme_id"),
                resultSet.getString("theme_name"),
                resultSet.getString("description"),
                resultSet.getString("thumbnail")
        );
        return new Reservation(
                resultSet.getLong("reservation_id"),
                resultSet.getString("name"),
                resultSet.getObject("date", LocalDate.class),
                reservationTime, theme
        );
    };

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Reservation reservation) {
        String sql = """
                INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setObject(2, reservation.getDate());
            ps.setLong(3, reservation.getTime().getId());
            ps.setLong(4, reservation.getTheme().getId());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Reservation> findAll() {
        String sql = """
                SELECT r.id as reservation_id,
                       r.name,
                       r.date,
                       rt.id as time_id,
                       rt.start_at,
                       th.id as theme_id,
                       th.name as theme_name,
                       th.description,
                       th.thumbnail
                FROM reservation as r
                INNER JOIN reservation_time as rt ON r.time_id=rt.id
                INNER JOIN theme as th ON r.theme_id=th.id
                ORDER BY r.date, rt.start_at, r.id
                """;
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    public List<Reservation> findAllByName(String name) {
        String sql = """
                SELECT r.id as reservation_id,
                r.name,
                r.date,
                rt.id as time_id,
                rt.start_at,
                th.id as theme_id,
                th.name as theme_name,
                th.description,
                th.thumbnail
                FROM reservation as r
                INNER JOIN reservation_time as rt ON r.time_id = rt.id
                INNER JOIN theme as th ON r.theme_id=th.id
                WHERE r.name = ?
                ORDER BY r.date, rt.start_at, r.id
                """;
        return jdbcTemplate.query(sql, reservationRowMapper, name);
    }

    public Optional<Reservation> findByIdAndName(Long id, String name) {
        String sql = """
                SELECT r.id as reservation_id,
                r.name,
                r.date,
                rt.id as time_id,
                rt.start_at,
                th.id as theme_id,
                th.name as theme_name,
                th.description,
                th.thumbnail
                FROM reservation as r
                INNER JOIN reservation_time as rt ON r.time_id = rt.id
                INNER JOIN theme as th ON r.theme_id = th.id
                WHERE r.id = ? AND r.name = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, reservationRowMapper, id, name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int update(Long id, String name, LocalDate date, Long timeId) {
        String sql = """
                UPDATE reservation
                SET `date` = ?, time_id = ?
                WHERE id = ? AND `name` = ?
                """;
        return jdbcTemplate.update(sql, date, timeId, id, name);
    }

    public boolean existsByTimeId(Long timeId) {
        String sql = """
                SELECT COUNT(*) FROM reservation WHERE time_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, timeId);

        return count != null && count > 0;
    }

    public boolean existsByThemeId(Long themeId) {
        String sql = """
                SELECT COUNT(*) FROM reservation WHERE theme_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, themeId);

        return count != null && count > 0;
    }

    public boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId) {
        String sql = """
                SELECT COUNT(*) FROM reservation WHERE date = ? AND time_id = ? AND theme_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, date, timeId, themeId);

        return count != null && count > 0;
    }

    public boolean isSameDateAndTime(Long id, LocalDate date, Long timeId) {
        String sql = """
                SELECT COUNT(*) FROM reservation WHERE id = ? AND date = ? AND time_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, date, timeId);

        return count != null && count > 0;
    }

    public int delete(Long id) {
        String sql = """
                DELETE FROM reservation WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    public int deleteByIdAndName(Long id, String name) {
        String sql = """
                DELETE FROM reservation WHERE id = ? AND name = ?
                """;
        return jdbcTemplate.update(sql, id, name);
    }
}
