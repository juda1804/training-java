package com.epam.homework.model;

import com.github.javafaker.Faker;

public record Customer(Integer id, String name, int loyaltyPoints, String email) {
  private static final Faker faker = new Faker();

  public static Customer random() {
    return new Customer(
        null,
        faker.name().fullName(),
        faker.random().nextInt(0, 150),
        faker.internet().emailAddress());
  }
}
