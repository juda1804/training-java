package com.epam.homework.config;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;


@Component
public class DatabaseInitializer implements InitializingBean{

    private final JdbcTemplate jdbcTemplate;
    private final Faker faker;
    private final Random random;

    @Autowired
    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        faker = new Faker();
        random = new Random();
    }

    @Override
    public void afterPropertiesSet() {
        deleteTables();
        init();
        populateUsers(1000);
        populateFriendships(70000);
        populatePosts(10000);
        populateLikes(350000);
    }

    public void init() {
        System.out.println("Initializing database...");
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255),
                    surname VARCHAR(255),
                    birthdate DATE
                );
                """;
        String createFriendshipsTable = """
                CREATE TABLE IF NOT EXISTS friendships(
                    userid1 BIGINT REFERENCES users(id),
                    userid2 BIGINT REFERENCES users(id),
                    timestamp TIMESTAMP,
                    PRIMARY KEY (userid1, userid2)
                )
                """;
        String createPostsTable = """
                CREATE TABLE IF NOT EXISTS posts(
                    id SERIAL PRIMARY KEY,
                    userId BIGINT REFERENCES users(id),
                    text TEXT,
                    timestamp TIMESTAMP
                );
                """;
        String createLikesTable = """
                CREATE TABLE IF NOT EXISTS likes(
                    postId BIGINT REFERENCES posts(id),
                    userId BIGINT REFERENCES users(id),
                    timestamp TIMESTAMP,
                    PRIMARY KEY (postId, userId)
                );
                """;
        jdbcTemplate.execute(createUsersTable);
        jdbcTemplate.execute(createFriendshipsTable);
        jdbcTemplate.execute(createPostsTable);
        jdbcTemplate.execute(createLikesTable);
    }

    public void deleteTables() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS likes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS posts");
        jdbcTemplate.execute("DROP TABLE IF EXISTS friendships");
        jdbcTemplate.execute("DROP TABLE IF EXISTS users");
    }

    public void populateUsers(int amount) {
        String sql = "INSERT INTO users (name, surname, birthdate) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < amount; ++i) {
            String name = faker.name().firstName();
            String surname = faker.name().lastName();
            LocalDate birthdate = LocalDate.of(1950 + random.nextInt(50), random.nextInt(12) + 1, random.nextInt(28) + 1);
            batchArgs.add(new Object[]{name, surname, birthdate});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.printf("Inserted %d users successfully.\n", amount);
    }

    public void populateFriendships(int amount) {
        String sql = "INSERT INTO friendships (userid1, userid2, timestamp) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        Set<IdPair> currentFriendships = new HashSet<>();
        int generated = 0;

        while(generated < amount) {
            long user1 = random.nextInt(1000)+ 1;
            long user2 = random.nextInt(1000)+ 1;
            var pair = new IdPair(user1, user2);
            if(currentFriendships.contains(pair) || user1 == user2) continue;
            currentFriendships.add(pair);
            var timestamp = LocalDate.of(2025, random.nextInt(12) + 1, random.nextInt(28) + 1);
            batchArgs.add(new Object[]{user1, user2, timestamp});
            generated++;
        }
        currentFriendships.clear();
        jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.printf("Inserted %d friendships successfully.\n", amount);
    }

    public void populatePosts(int amount) {
        String sql = "INSERT INTO posts (userId, text, timestamp) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for(int i =0; i< amount; ++i) {
            long userId = random.nextInt(1000)+1;
            var text = faker.lorem().sentence();
            var timestamp = LocalDate.of(2025, random.nextInt(12) + 1, random.nextInt(28) + 1);
            batchArgs.add(new Object[]{userId, text, timestamp});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.printf("Inserted %d posts successfully.\n", amount);
    }

    public void populateLikes(int amount) {
        String sql = "INSERT INTO likes (postId, userId, timestamp) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        Set<IdPair> currentLikes = new HashSet<>();
        int generated = 0;

        while(generated < amount) {
            long userId = random.nextInt(1000)+1;
            long postId = random.nextInt(10000)+1;
            var pair = new IdPair(postId, userId);
            if(currentLikes.contains(pair)) continue;
            currentLikes.add(pair);
            var timestamp = LocalDate.of(2025, random.nextInt(12) + 1, random.nextInt(28) + 1);
            batchArgs.add(new Object[]{postId, userId, timestamp});
            generated++;
        }
        currentLikes.clear();
        jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.printf("Inserted %d likes succesffully.\n", amount);
    }

    private record IdPair(Long id1, Long id2){}
}
