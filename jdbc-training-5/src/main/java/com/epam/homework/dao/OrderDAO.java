package com.epam.homework.dao;

import static javax.swing.UIManager.get;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDAO {

    private final SimpleJdbcCall processOrderCall;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;


    @Autowired
    public OrderDAO(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.processOrderCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("process_order")
                .declareParameters(
                        new SqlParameter("customer_id", Types.INTEGER),
                        new SqlParameter("product_ids", Types.ARRAY, "int4"),
                        new SqlParameter("quantities", Types.ARRAY, "int4")
                );
        this.dataSource = dataSource;
    }

    public double processOrder(int customerId, Integer[] productIds, Integer[] quantities) {
        try (var connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {

            // Prepare input parameters
            Map<String, Object> inParams = new HashMap<>();
            inParams.put("customer_id", customerId);
            inParams.put("product_ids", connection.createArrayOf("int4", productIds));
            inParams.put("quantities", connection.createArrayOf("int4", quantities));

            Map<String, Object> result = processOrderCall.execute(inParams);

            Object o = result.get("#result-set-1");
            List<Object> objects = (List<Object>) o;
            Map<String, BigDecimal> result2  = (Map<String, BigDecimal>) objects.get(0);

            BigDecimal result1 = result2.get("result");
            return Optional.ofNullable(result1.doubleValue()).orElse(0d);
        } catch (SQLException ex) {
            throw new RuntimeException("An error occurred while calling stored procedure", ex);
        }
    }


}
