package com.epam.homework.dao;

import com.epam.homework.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CustomerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Customer customer) {
        String sql = "INSERT INTO Customers(name, loyalty_points, email) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql, customer.name(), customer.loyaltyPoints(), customer.email());
        System.out.printf("Successfully created customer with name [%s]\n", customer.name());
    }
}
