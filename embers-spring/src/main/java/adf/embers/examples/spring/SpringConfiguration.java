package adf.embers.examples.spring;

import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbcDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class SpringConfiguration {

    public static final String JDBC_URL = "jdbc:hsqldb:mem:acceptance-test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    @Bean
    public DataSource dataSource() throws SQLException {
        DriverManager.registerDriver(jdbcDriver.driverInstance);

        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase(JDBC_URL);
        dataSource.setUser(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setLoginTimeout(5);

        return dataSource;
    }

}
