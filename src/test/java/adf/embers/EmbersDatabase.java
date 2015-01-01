package adf.embers;

import adf.embers.query.persistence.QueriesDao;
import adf.embers.query.persistence.Query;
import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbcDriver;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmbersDatabase {

    public static final String JDBC_URL = "jdbc:hsqldb:mem:test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
    private DataSource dataSource;
    private String jdbcUrl;

    public EmbersDatabase(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void startInmemoryDatabase() throws Exception {
        DriverManager.registerDriver(jdbcDriver.driverInstance);

        this.dataSource = new JDBCDataSource();
        ((JDBCDataSource)this.dataSource).setDatabase(jdbcUrl);
        ((JDBCDataSource)this.dataSource).setUser(USERNAME);
        ((JDBCDataSource)this.dataSource).setPassword(PASSWORD);
        this.dataSource.setLoginTimeout(5);
    }

    public void createTableQueries() throws SQLException {
        Handle handle = new DBI(dataSource).open();
        handle.execute("CREATE TABLE queries(" +
                "id INTEGER  GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                "name VARCHAR(50)," +
                "description VARCHAR(1000)," +
                "sql VARCHAR(2000))");
        handle.close();
    }

    public void insertAllQueries() throws SQLException {
        DBI dbi = new DBI(dataSource);
        QueriesDao open = dbi.open(QueriesDao.class);
        open.save(new Query("allQueries", "Shows all the available queries", "select name, description from queries order by name"));
        dbi.close(open);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
