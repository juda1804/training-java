package com.epam.homework.model;

import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table
public record Like(Long postId, Long userId, Timestamp timestamp) {
}
