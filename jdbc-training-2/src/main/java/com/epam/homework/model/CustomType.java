package com.epam.homework.model;

import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;

public enum CustomType {
    INT,
    REAL,
    BOOLEAN,
    TEXT,
    TIMESTAMP;

    public String asPostgresType() {
        return switch (this) {
            case INT -> "INT";
            case BOOLEAN -> "BOOLEAN";
            case TEXT -> "TEXT";
            case REAL -> "REAL";
            case TIMESTAMP -> "TIMESTAMP";
        };
    }

    private static Faker faker = new Faker();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String generateRandom() {
        return switch (this) {
            case INT ->
                    Integer.toString(CustomType.faker.number().numberBetween(-100000, 100000));
            case BOOLEAN ->
                    Boolean.toString(CustomType.faker.bool().bool());
            case TEXT ->
                    String.format("'%s'", CustomType.faker.rockBand().name().replace("'", ""));
            case REAL ->
                    Double.toString(CustomType.faker.number().randomDouble(8, -100000, 100000));
            case TIMESTAMP ->
                    String.format("'%s'", formatter.format(CustomType.faker.date().birthday()));
        };
    }
}
