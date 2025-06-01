package adf.embers.examples.spring;

import adf.embers.tools.EmbersDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;

/**
 * Configuration for setting up the test data source using H2 in-memory database.
 * This configuration is only active when the 'test' profile is active.
 */
@Configuration
@Profile("test")
public class SpringDataSourceConfiguration implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(SpringDataSourceConfiguration.class);
    
    private final EmbersDatabase embersDatabase;
    private final String jdbcUrl;
    private final String username;
    private final String password;

    /**
     * Constructor with configurable properties.
     * Default values are provided for test environment.
     */
    public SpringDataSourceConfiguration(
            @Value("${spring.datasource.url:jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}") String jdbcUrl,
            @Value("${spring.datasource.username:sa}") String username,
            @Value("${spring.datasource.password:}") String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.embersDatabase = new EmbersDatabase(jdbcUrl);
    }

    /**
     * Initialize the in-memory database and create necessary tables.
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing in-memory H2 database at: {}", jdbcUrl);
            this.embersDatabase.startInMemoryDatabase();
            this.embersDatabase.createTableQueries();
            this.embersDatabase.createTableQueriesStatistics();
            this.embersDatabase.createTableQueryResultCache();
            log.info("Successfully initialized in-memory H2 database");
        } catch (Exception e) {
            log.error("Failed to initialize in-memory H2 database", e);
            throw new IllegalStateException("Failed to initialize in-memory H2 database", e);
        }
    }

    /**
     * Configures and provides a DataSource bean.
     * @return Configured DataSource
     */
    @Bean
    public DataSource dataSource() {
        log.info("Configuring DataSource with URL: {}", jdbcUrl);
        try {
            // Load the H2 driver class
            Class.forName("org.h2.Driver");
            
            // Create and configure the DataSource
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl(jdbcUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            
            log.info("Successfully configured DataSource");
            return dataSource;
        } catch (ClassNotFoundException e) {
            log.error("Failed to load H2 JDBC Driver", e);
            throw new IllegalStateException("H2 JDBC Driver not found. Make sure it's included in the classpath.", e);
        } catch (Exception e) {
            log.error("Failed to configure DataSource", e);
            throw new RuntimeException("Failed to configure DataSource", e);
        }
    }
    
    /**
     * Clean up resources when the application context is destroyed.
     */
    @Override
    @PreDestroy
    public void destroy() {
        log.info("Shutting down in-memory H2 database");
        try {
            this.embersDatabase.shutdownInMemoryDatabase();
        } catch (Exception e) {
            log.warn("Error while shutting down in-memory H2 database", e);
        }
    }
}
