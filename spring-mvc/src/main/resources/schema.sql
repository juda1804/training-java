-- Users table with auto-increment for H2
CREATE TABLE USERS (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255),
                       email VARCHAR(255)
);

-- Events table with auto-increment for H2
CREATE TABLE EVENTS (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),
                        date_time TIMESTAMP
);

-- Tickets table with auto-increment and foreign keys to users and events
CREATE TABLE TICKETS (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT,
                         event_id BIGINT,
                         place INT,
                         category VARCHAR(50),
    -- Define the relationships
                         CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);