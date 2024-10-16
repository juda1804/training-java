package com.epam.homework.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInit {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseInit(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS Files(
                    id SERIAL PRIMARY KEY,
                    name TEXT,
                    file_type TEXT,
                    size BIGINT CHECK (size <= 210 * 1024 * 1024),
                    content BYTEA NOT NULL
                );
                """;

        jdbcTemplate.execute(sql);
        System.out.println("Successfully created table for Files");
    }
}
