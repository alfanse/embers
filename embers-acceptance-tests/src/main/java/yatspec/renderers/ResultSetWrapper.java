package yatspec.renderers;

import java.util.List;
import java.util.Map;

/**
 * This class holds the result of a DB call for later rendering with the ResultSetRender
 * e.g.
 * DBI dbi = new DBI(dataSource);
 * try (Handle handle = dbi.open()) {
 * Query<Map<String, Object>> q = handle.createQuery("select * from " + QueryDao.TABLE_QUERIES + " order by " + QueryDao.COL_ID);
 * log("Database Queries", new ResultSetWrapper(q.list()));
 */
public class ResultSetWrapper {
    private List<Map<String, Object>> resultSet;

    public ResultSetWrapper(List<Map<String, Object>> resultSet) {
        this.resultSet = resultSet;
    }

    public List<Map<String, Object>> getResultSet() {
        return resultSet;
    }
}
