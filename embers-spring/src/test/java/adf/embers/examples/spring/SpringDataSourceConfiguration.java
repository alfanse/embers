package adf.embers.examples.spring;

import adf.embers.tools.EmbersDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringDataSourceConfiguration {

    private final EmbersDatabase embersDatabase;

    public SpringDataSourceConfiguration() throws Exception {
        embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
        embersDatabase.createTableQueriesStatistics();
        embersDatabase.createTableQueryResultCache();
    }

    @Bean
    public DataSource dataSource() {
        return embersDatabase.getDataSource();
    }
}
