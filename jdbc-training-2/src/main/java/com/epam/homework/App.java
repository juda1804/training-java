package com.epam.homework;

import com.epam.homework.config.AppConfig;
import com.epam.homework.model.CustomType;
import com.epam.homework.model.TableDefinition;
import com.epam.homework.service.DatabaseService;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    private AnnotationConfigApplicationContext context;

    // Constructor that accepts the properties file path
    public App(String propertiesFilePath) {
        this.context = new AnnotationConfigApplicationContext();
        this.context.getEnvironment().getSystemProperties().put("propertiesFilePath", propertiesFilePath);
        this.context.register(AppConfig.class);
        this.context.refresh();
    }

    // Method to get the context for testing purposes
    public AnnotationConfigApplicationContext getContext() {
        return this.context;
    }

    // The main method for running the application
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Pass the properties file as argument");
            System.exit(1);
        }

        // Creating an instance of App
        App app = new App(args[0]);
        app.run();
    }

    // Method that actually runs the business logic
    public void run() {
        var service = context.getBean(DatabaseService.class);
        service.generateTables(generateRandomTableDefinitions());
    }

    private List<TableDefinition> generateRandomTableDefinitions() {
        var types = List.of(CustomType.INT, CustomType.REAL, CustomType.BOOLEAN, CustomType.TEXT, CustomType.TIMESTAMP);
        var random = new Random();
        var rows = random.nextInt(50);
        var columns = random.nextInt(20);
        var tableDefinitions = IntStream.range(1, 31)
                .mapToObj(i -> new TableDefinition(
                        columns,
                        types,
                        rows == 0 ? 10 : rows));
        return tableDefinitions.toList();
    }
}
