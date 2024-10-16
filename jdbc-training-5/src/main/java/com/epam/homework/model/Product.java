package com.epam.homework.model;

import com.github.javafaker.Faker;

public record Product(Integer id, String title, double price, int stock) {

  private static final Faker faker = new Faker();

  public static Product random() {
    return new Product(
        null,
        faker.beer().name(),
        (double) faker.random().nextInt(1000, 50000),
        faker.random().nextInt(10, 80));
  }
}
