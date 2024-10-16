package com.epam.homework.repository;

import com.epam.homework.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FileRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FileRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public File save(File file) {
        String sql = "INSERT INTO Files (name, file_type, size, content) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, file.name());
            ps.setString(2, file.type());
            ps.setLong(3, file.size());
            ps.setBytes(4, file.content());
            return ps;
        }, keyHolder);

        return new File(
                (Integer)keyHolder.getKeyList().get(0).get("id"),
                file.name(),
                file.type(),
                file.size(),
                file.content()
        );
    }

    public File getById(Integer id) {
        String sql = "SELECT * FROM Files WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, fileRowMapper(), id);
    }

    public List<File> getAll() {
        String sql = "SELECT * FROM Files;";
        return jdbcTemplate.query(sql, fileRowMapper());
    }

    private RowMapper<File> fileRowMapper() {
        return (rs, rowNum) -> new File(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("file_type"),
                rs.getLong("size"),
                rs.getBytes("content")
        );
    }
}
