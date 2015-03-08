package adf.embers.tools;

import adf.embers.query.persistence.QueryDao;
import adf.embers.query.persistence.QueryStatisticsDao;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbcDriver;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static adf.embers.query.persistence.QueryDao.*;
import static adf.embers.query.persistence.QueryStatisticsDao.*;

public class EmbersDatabase {

    public static final String JDBC_URL = "jdbc:hsqldb:mem:acceptance-test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private final String jdbcUrl;
    private DataSource dataSource;

    public EmbersDatabase(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void startInMemoryDatabase() throws Exception {
        DriverManager.registerDriver(jdbcDriver.driverInstance);

        this.dataSource = new JDBCDataSource();
        ((JDBCDataSource) this.dataSource).setDatabase(jdbcUrl);
        ((JDBCDataSource) this.dataSource).setUser(USERNAME);
        ((JDBCDataSource) this.dataSource).setPassword(PASSWORD);
        this.dataSource.setLoginTimeout(5);
    }

    public void shutdownInMemoryDatabase() {
        try (Connection connection = getDataSource().getConnection()) {
            Statement st = connection.createStatement();
            st.execute("SHUTDOWN");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /** This table holds the named queries */
    public void createTableQueries() {
        executeSql("CREATE TABLE " + TABLE_QUERIES + "(" +
                QueryDao.COL_ID + " INTEGER  GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                COL_NAME + " VARCHAR(50)," +
                COL_DESCRIPTION + " VARCHAR(1000)," +
                COL_SQL + " VARCHAR(2000))");
    }

    /** This table holds the execution performance of queries */
    public void createTableQueriesStatistics() {
        executeSql("CREATE TABLE " + TABLE_QUERIES_STATISTICS + "(" +
                QueryStatisticsDao.COL_ID + " INTEGER  GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                COL_QUERY_NAME + " VARCHAR(50), " +
                COL_DATE_EXECUTED + " TIMESTAMP, " +
                COL_DURATION + " BIGINT , " +
                COL_RESULT + " VARCHAR(100) )");
    }

    private void executeSql(String sql) {
        Handle handle = new DBI(dataSource).open();
        handle.execute(sql);
        handle.close();
    }

}
