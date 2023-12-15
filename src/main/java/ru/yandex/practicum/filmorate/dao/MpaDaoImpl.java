package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Motion;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Motion> getAllMpa() {
        String query = "SELECT * FROM mpa";

        log.info("Список рейтингов отправлен");
        return jdbcTemplate.query(query, motionRowMapper());
    }

    @Override
    public Motion getMpaById(Integer id) throws SQlDataException {
        String query = "SELECT * FROM mpa WHERE id = ?";

        try{
            log.info("Рейтинг по id {} отправлен", id);
            return jdbcTemplate.queryForObject(query, motionRowMapper(), id);
        } catch (DataAccessException e) {
            log.info("Рейтинга с таким id {} нет", id);
            throw new SQlDataException("Рейтинга с таким id " + id + " нет");
        }
    }

    private RowMapper<Motion> motionRowMapper() {
        return ((rs, rowNum) -> (new Motion(rs.getInt("id"), rs.getString("motion"))));
    }
}
