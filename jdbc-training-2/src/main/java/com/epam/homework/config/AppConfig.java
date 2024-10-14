package com.epam.homework.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "com.epam.homework")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer(Environment environment) throws IOException {
        var path=environment.getProperty("propertiesFilePath");
        var properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(path));
        var configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(properties);
        return configurer;
    }

    @Value("${db.driver}")
    private String driver;

    @Value("${db.url}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${c3p0.minPoolSize:5}")
    private int minPoolSize;

    @Value("${c3p0.maxPoolSize:20}")
    private int maxPoolSize;

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driver);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxIdleTime(300);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
