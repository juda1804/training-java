package org.booking.config;

import com.thoughtworks.xstream.security.WildcardTypePermission;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.booking.converter.TicketMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.booking.data.repository")
@ComponentScan(basePackages = "org.booking")
@PropertySource("classpath:application.properties")
@EnableJms
public class AppConfig {

    @Bean
    public DataSource postgresDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tickets_database?currentSchema=tickets_database");
        dataSource.setUsername("epam-user");
        dataSource.setPassword("pwd123");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("org.booking.model");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        factoryBean.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "update");
        return factoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setUserName("artemis");
        factory.setPassword("artemis");
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory connectionFactory) {
        TicketMessageConverter ticketMessageConverter = new TicketMessageConverter();
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(ticketMessageConverter);
        return jmsTemplate;
    }


    @Bean
    public XStreamMarshaller xstreamMarshaller() {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setTypePermissions(new WildcardTypePermission(new String[]{"org.booking.model.*"}));
        marshaller.setAliases(Map.of(
                "users", "org.booking.model.Users",
                "user", "org.booking.model.User",
                "events", "org.booking.model.Events",
                "event", "org.booking.model.Event",
                "tickets", "org.booking.model.Tickets",
                "ticket", "org.booking.model.Ticket"
        ));
        marshaller.setAnnotatedClasses(
                org.booking.model.Users.class,
                org.booking.model.User.class,
                org.booking.model.Events.class,
                org.booking.model.Event.class,
                org.booking.model.Tickets.class,
                org.booking.model.Ticket.class
        );
        return marshaller;
    }
}