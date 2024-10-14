package com.epam.homework.service;

import com.epam.homework.model.CustomType;
import com.epam.homework.model.TableDefinition;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatabaseService {

    private final Faker faker;
    private final Random random;

    public DatabaseService() {
        faker = new Faker();
        random = new Random();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void generateTables(List<TableDefinition> definitions) {
        definitions.forEach(this::generateTable);
    }

    public void generateTable(TableDefinition definition) {
        // TABLE CREATION
        StringBuilder tableQuery = new StringBuilder();
        var tableName = faker
                .name()
                .nameWithMiddle()
                .replace(' ', '_')
                .replace(".", "")
                .replace("'", "");
        var tableDefinition = generateTableMapping(definition.columnsAmount(), definition.customTypes());
        var columnsQuery = generateQueryForColumns(tableDefinition);
        System.out.printf("Creating table with name [%s] and %d columns out of %d types randomly...\n", tableName, definition.columnsAmount(), definition.customTypes().size());
        tableQuery.append(String.format("CREATE TABLE %s (\n%s);", tableName, columnsQuery));
        System.out.println("============================================");
        System.out.println(tableQuery);
        System.out.println("============================================");
        jdbcTemplate.execute(tableQuery.toString());

        // VALUE FILLING
        var values = generateValues(definition.elementsAmount(), tableDefinition, tableName);
        System.out.printf("About to fill table [%s] with %d values...\n", tableName, definition.elementsAmount());
        System.out.println("============================================");
        System.out.println(values);
        System.out.println("============================================");
        jdbcTemplate.execute(values);
    }

    Map<String, CustomType> generateTableMapping(int columns, List<CustomType> types) {
        var result = new HashMap<String, CustomType>();
        var names = generateNames(columns).stream().toList();
        for(var name: names) {
            var randomIndex = random.nextInt(types.size());
            var type = types.get(randomIndex);
            result.put(name, type);
        }
        return result;
    }

    String generateQueryForColumns(Map<String, CustomType> columnDefinition) {
        StringBuilder sb = new StringBuilder();
        for(var entry: columnDefinition.entrySet()) {
            var columnName = entry.getKey();
            var type = entry.getValue();
            sb.append(String.format("%s %s,\n", columnName, type.asPostgresType()));
        }
        //remove last comma
        if(sb.lastIndexOf(",") != -1)
            sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    Set<String> generateNames(int amount) {
        Set<String> names = new HashSet<>();
        while(names.size() < amount) {
            String name = faker.color().name().replace(' ', '_');
            names.add(name);
        }
        return names;
    }

    String generateValues(int amount, Map<String, CustomType> tableDefinition, String tableName) {
        StringBuilder query = new StringBuilder();
        for(int i=0; i<amount; ++i) {
            query.append(String.format("INSERT INTO %s VALUES(", tableName));
            for(var entry: tableDefinition.entrySet()) {
                var randomValue = entry.getValue().generateRandom();
                query.append(randomValue).append(", ");
            }
            if(query.lastIndexOf(",") != -1)
                query.deleteCharAt(query.lastIndexOf(","));

            query.append(");\n");
        }
        return query.toString();
    }
}
