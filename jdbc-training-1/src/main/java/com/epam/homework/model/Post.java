package com.epam.homework.model;

import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table
public record Post(Long id, Long userId, String text, Timestamp timestamp) {
}
