package com.epam.homework.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void createProcessOrderFunction() {
        String sql = """
        CREATE OR REPLACE FUNCTION process_order(
            IN customer_id INT,
            IN product_ids INT[],
            IN quantities INT[]
        )
        RETURNS DECIMAL(10, 2)
        LANGUAGE plpgsql
        AS $$
        DECLARE
            c_total_amount DECIMAL(10, 2) := 0;
            product_price DECIMAL(10, 2);
            discount DECIMAL(10, 2) := 0;
            stock_available INT;
            product_id INT;
            quantity INT;
            order_id INT;
            loyalty_points INT;
            i INT;
        BEGIN
            -- Fetch loyalty points for the customer
            SELECT c.loyalty_points INTO loyalty_points
            FROM Customers c
            WHERE id = customer_id;
        
            -- Create a new order with an initial total amount of 0
            INSERT INTO Orders(customer_id, date_time, total_amount)
            VALUES (customer_id, NOW(), 0)
            RETURNING id INTO order_id;
            -- Process each product in the order
            FOR i IN 1..array_length(product_ids, 1) LOOP
                -- Get the current product ID and quantity
                product_id := product_ids[i];
                quantity := quantities[i];
        
                -- Fetch the price and available stock for the product
                SELECT price, stock INTO product_price, stock_available
                FROM products
                WHERE id = product_id;
        
                -- Ensure there is enough stock before proceeding
                IF stock_available < quantity THEN
                    RAISE EXCEPTION 'Not enough stock for product % quantity %, stock available %', product_id, quantity, stock_available;
                END IF;
        
                -- Insert an order item for the product
                INSERT INTO Order_Items (order_id, product_id, quantity, price)
                VALUES (order_id, product_id, quantity, product_price);
        
                -- Update the total amount
                c_total_amount := c_total_amount + (product_price * quantity);
        
                -- Deduct the quantity from the product's stock
                UPDATE products
                SET stock = stock - quantity
                WHERE id = product_id;
            END LOOP;
        
            -- Apply a discount if the customer has more than 100 loyalty points
            IF loyalty_points > 100 THEN
                discount := c_total_amount * 0.1; -- 10% discount
            END IF;
        
            -- Update the total amount after applying the discount
            c_total_amount := c_total_amount - discount;
        
            -- Update the order's total amount
            UPDATE Orders o
            SET total_amount = c_total_amount
            WHERE id = order_id;
        
            -- Return the total amount as the function result
            RETURN c_total_amount;
        END;
        $$;
        """;

        jdbcTemplate.execute(sql);

        System.out.println("Stored procedure created successfully...");
    }

    public void createTables() {
        String customers = """
            CREATE TABLE IF NOT EXISTS Customers(
                id SERIAL PRIMARY KEY,
                name TEXT,
                loyalty_points INT,
                email TEXT
            );
        """;

        String products = """
            CREATE TABLE IF NOT EXISTS Products(
                id SERIAL PRIMARY KEY,
                title TEXT,
                price DECIMAL(10, 2),
                stock INT
            );
        """;

        String orders = """
            CREATE TABLE IF NOT EXISTS Orders(
                id SERIAL PRIMARY KEY,
                customer_id INT REFERENCES Customers(id),
                date_time TIMESTAMP,
                total_amount DECIMAL(10, 2)
            );
        """;

        String orderItems = """
            CREATE TABLE IF NOT EXISTS Order_Items(
                id SERIAL PRIMARY KEY,
                order_id INT REFERENCES Orders(id),
                product_id INT REFERENCES Products(id),
                quantity INT,
                price DECIMAL(10, 2)
            );
        """;

        jdbcTemplate.execute(customers);
        jdbcTemplate.execute(products);
        jdbcTemplate.execute(orders);
        jdbcTemplate.execute(orderItems);
        System.out.println("Successfully created tables...");
    }

    public void dropTables() {
        jdbcTemplate.execute("DROP TABLE  Customers CASCADE;");
        jdbcTemplate.execute("DROP TABLE  Products CASCADE;");
        jdbcTemplate.execute("DROP TABLE  Orders CASCADE;");
        jdbcTemplate.execute("DROP TABLE  Order_Items CASCADE;");
        jdbcTemplate.execute("DROP FUNCTION IF EXISTS process_order(INT, INT[], INT[]);");
        System.out.println("Deleted tables sucessfully...");
    }
}
