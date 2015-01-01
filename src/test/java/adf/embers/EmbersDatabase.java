package adf.embers;

import org.hsqldb.jdbcDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmbersDatabase {

    public static final String JDBC_URL = "jdbc:hsqldb:mem:test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";

    public void startInmemoryDatabase() throws Exception {
        DriverManager.registerDriver(jdbcDriver.driverInstance);

        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        assertThat(connection.isClosed()).isFalse();

        createTableQueries(connection);
    }

    public void createTableQueries(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE queries(" +
                    "id INTEGER IDENTITY, " +
                    "name VARCHAR(50)," +
                    "description VARCHAR(1000)," +
                    "sql VARCHAR(2000))");
        statement.close();
    }

}
