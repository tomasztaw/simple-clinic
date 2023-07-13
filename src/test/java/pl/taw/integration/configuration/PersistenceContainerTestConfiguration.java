package pl.taw.integration.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration
public class PersistenceContainerTestConfiguration {

    public static final String POSTGRES_USERNAME = "username";
    public static final String POSTGRES_PASSWORD = "password";
    public static final String POSTGRESQL = "postgresql";
    public static final String POSTGRESQL_CONTAINER = "postgres:15.3";

    @Bean
    @Qualifier(POSTGRESQL)
    PostgreSQLContainer<?> postgresqlContainer() {
        PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>(POSTGRESQL_CONTAINER)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD);
        postgresqlContainer.start();
//        System.setProperty("spring.datasource.username", postgresqlContainer.getUsername());
//        System.setProperty("spring.datasource.password", postgresqlContainer.getPassword());
        return postgresqlContainer;
    }

    @Bean
    DataSource dataSource(final PostgreSQLContainer<?> container) {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .driverClassName(container.getDriverClassName())
            .url(container.getJdbcUrl())
            .username(container.getUsername())
            .password(container.getPassword())
            .build();
    }
}
