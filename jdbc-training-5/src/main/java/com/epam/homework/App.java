package com.epam.homework;

import com.epam.homework.config.AppConfig;
import com.epam.homework.dao.CustomerDAO;
import com.epam.homework.dao.DatabaseDAO;
import com.epam.homework.dao.OrderDAO;
import com.epam.homework.dao.ProductDAO;
import com.epam.homework.model.Customer;
import com.epam.homework.model.Product;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.IntStream;

public class App {

    private static final int CUSTOMER_COUNT = 10;
    private static final int PRODUCT_COUNT = 40;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        setupDatabase(context);
        createCustomers(context);
        createProducts(context);
        System.out.println("Processing order");
        processOrder(context, 1, new Integer[]{1, 4, 8, 10, 21}, new Integer[] {3, 5, 1, 1, 2});
        System.out.println("Processing product");
        System.out.println("Cleaning database");
        deleteDatabase(context);

    }

    private static void setupDatabase(ApplicationContext context) {
        var databaseDAO = context.getBean(DatabaseDAO.class);
        databaseDAO.createTables();
        databaseDAO.createProcessOrderFunction();
    }

    private static void deleteDatabase(ApplicationContext context) {
        var databaseDAO = context.getBean(DatabaseDAO.class);
        databaseDAO.dropTables();
    }

    private static void createCustomers(ApplicationContext context) {
        var customerDAO = context.getBean(CustomerDAO.class);
        IntStream.range(1, CUSTOMER_COUNT + 1).forEach(i -> {
            var customer = Customer.random();
            customerDAO.save(customer);
        });
    }

    private static void createProducts(ApplicationContext context) {
        var productDAO = context.getBean(ProductDAO.class);
        IntStream.range(1, PRODUCT_COUNT + 1).forEach(i -> {
            var product = Product.random();
            productDAO.save(product);
        });
    }

    private static void processOrder(ApplicationContext context, int customerId, Integer[] productIds, Integer[] quantities) {
        var orderDAO = context.getBean(OrderDAO.class);
        //BigDecimal result = orderDAO.processOrder(customerId, productIds, quantities);
        double result = orderDAO.processOrder(customerId, productIds, quantities);
        System.out.printf("Result from processing order: %f%n", result);
    }
}
