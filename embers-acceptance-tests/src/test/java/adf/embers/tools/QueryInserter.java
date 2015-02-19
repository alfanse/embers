package adf.embers.tools;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class QueryInserter {

    public static final String ALL_QUERIES = "allQueries";
    private DataSource dataSource;

    public QueryInserter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAllQueries() {
        insertQuery(new Query(ALL_QUERIES, "Shows all the available queries", "select id, name, description, sql from queries order by name"));
    }

    public void insertQuery(Query query) {
        DBI dbi = new DBI(dataSource);
        QueryDao open = dbi.open(QueryDao.class);
        open.save(query);
        dbi.close(open);
    }
}
