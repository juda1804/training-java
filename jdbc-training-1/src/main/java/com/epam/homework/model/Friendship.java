package com.epam.homework.model;

import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table
public record Friendship(Long user1, Long user2, Timestamp timestamp) {
}
