package roomescape.theme.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.Theme;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ThemeDao {
    private final JdbcTemplate jdbcTemplate;

    public ThemeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Theme theme) {
        String sql = """
                INSERT INTO theme (name, description, thumbnail) VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, theme.getName());
            ps.setString(2, theme.getDescription());
            ps.setString(3, theme.getThumbnail());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Theme> findAll() {
        String sql = """
                SELECT id, name, description, thumbnail FROM theme
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Theme(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("thumbnail")
            );
        });
    }

    public Optional<Theme> findById(Long id) {
        String sql = """
                SELECT id, name, description, thumbnail FROM theme WHERE id = ?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
                return new Theme(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("thumbnail")
                );
            }, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int delete(Long id) {
        String sql = """
                DELETE FROM theme WHERE id = ?
                """;
        return jdbcTemplate.update(sql, id);
    }
}
