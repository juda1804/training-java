CREATE TABLE IF NOT EXISTS USERS (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS EVENTS (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    dt DATE,
    ticket_price REAL
);

CREATE TABLE IF NOT EXISTS TICKETS (
    id INT PRIMARY KEY,
    event_id INT references EVENTS(id),
    user_id INT references USERS(id),
    category VARCHAR(50),
    place INT
);

CREATE TABLE IF NOT EXISTS USER_ACCOUNTS (
    id INT PRIMARY KEY,
    user_id INT references USERS(id),
    amount REAL
);
