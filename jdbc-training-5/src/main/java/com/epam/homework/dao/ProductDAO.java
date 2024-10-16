package com.epam.homework.dao;

import com.epam.homework.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Product product) {
        String sql = "INSERT INTO Products(title, price, stock) VALUES(?, ?, ?);";

        jdbcTemplate.update(sql, product.title(), product.price(), product.stock());

        System.out.printf("Successfully created Product with name [%s] and price %f\n", product.title(), product.price());
    }
}
