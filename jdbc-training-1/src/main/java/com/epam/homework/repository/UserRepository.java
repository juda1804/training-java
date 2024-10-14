package com.epam.homework.repository;

import com.epam.homework.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository  {

    private JdbcTemplate jdbcTemplate;

    private final String query = """
        SELECT u.name, u.surname FROM users u
        JOIN (
            SELECT CASE 
                     WHEN userid1 < userid2 THEN userid1
                     ELSE userid2 
                   END as user_id
            FROM friendships
            GROUP BY user_id
            HAVING COUNT(*) > 100
        ) f ON u.id = f.user_id
        JOIN (
            SELECT l.userid FROM likes l
            WHERE EXTRACT(YEAR FROM l.timestamp) = 2025
              AND EXTRACT(MONTH FROM l.timestamp) = 3
            GROUP BY l.userid
            HAVING COUNT(l.userid) > 20
        ) l ON u.id = l.userid;
        """;

    public UserRepository(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    public List<User> findUsersWithMoreThanFriendsAndLikes() {
        return jdbcTemplate.query(query, userRowMapper);
    }

    public static RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        return user;
    };
}
