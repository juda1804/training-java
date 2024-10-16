package com.epam.homework.model;

import java.time.LocalDateTime;

public record Order(Integer id, int customerId, LocalDateTime dateTime, double total) {}
