package adf.embers.examples.spring;

import adf.embers.tools.EmbersDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

    public SpringDataSourceConfiguration() {
        this.embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
    }

    /**
     * Initialize the in-memory database and create necessary tables.
     */
    @PostConstruct
    public void initialize() throws Exception {
        this.embersDatabase.startInMemoryDatabase();
        this.embersDatabase.createTableQueries();
        this.embersDatabase.createTableQueriesStatistics();
        this.embersDatabase.createTableQueryResultCache();
    }

    /**
     * @return Configured DataSource
     */
    @Bean
    public DataSource dataSource() {
        return this.embersDatabase.getDataSource();
    }

    @Override
    @PreDestroy
    public void destroy() {
        try {
            this.embersDatabase.shutdownInMemoryDatabase();
        } catch (Exception e) {
            log.warn("Error while shutting down in-memory H2 database", e);
        }
    }
}
