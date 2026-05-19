package roomescape.schedule.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.schedule.model.Schedule;
import roomescape.theme.model.Theme;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Schedule> scheduleRowMapper = (rs, rowNum) -> {
        Theme theme = new Theme(
                rs.getLong("theme_id"),
                rs.getString("theme_name"),
                rs.getString("description"),
                rs.getString("image_url"),
                rs.getObject("required_time", LocalTime.class));
        return new Schedule(
                rs.getLong("schedule_id"),
                rs.getObject("start_at", LocalDateTime.class),
                theme
        );
    };

    public ScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long create(Schedule schedule) {
        String sql = "INSERT INTO schedule (theme_id, start_at, end_at) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, schedule.getTheme().getId());
            ps.setObject(2, schedule.getStartAt());

            LocalDateTime endAt = schedule.getStartAt()
                    .plusHours(schedule.getTheme().getRequiredTime().getHour())
                    .plusMinutes(schedule.getTheme().getRequiredTime().getMinute());
            ps.setObject(3, endAt);

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Schedule> findAll(Long themeId, LocalDate date) {
        String sql = """
                SELECT s.id AS schedule_id, 
                       s.start_at, 
                       s.end_at, 
                       t.id AS theme_id, 
                       t.name AS theme_name,
                       t.description,
                       t.image_url,
                       t.required_time
                FROM schedule s
                INNER JOIN theme t ON s.theme_id = t.id
                LEFT JOIN reservation r ON s.id = r.schedule_id
                WHERE s.theme_id = ?
                  AND s.start_at >= ? 
                  AND s.start_at < ?
                  AND r.id IS NULL
                """;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return jdbcTemplate.query(sql, scheduleRowMapper, themeId, startOfDay, endOfDay);
    }

    public boolean existsByThemeId(Long themeId) {
        String sql = "SELECT COUNT(*) FROM schedule WHERE theme_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, themeId);

        return count != null && count > 0;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM schedule WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Schedule> findById(Long id) {
        String sql = """
                SELECT s.id AS schedule_id,
                       s.start_at,
                       s.end_at,
                       t.id AS theme_id,
                       t.name AS theme_name,
                       t.description,
                       t.image_url,
                       t.required_time
                FROM schedule s
                INNER JOIN theme t ON s.theme_id = t.id
                WHERE s.id = ?
                """;

        try {
            Schedule schedule = jdbcTemplate.queryForObject(sql, scheduleRowMapper, id);
            return Optional.of(schedule);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
