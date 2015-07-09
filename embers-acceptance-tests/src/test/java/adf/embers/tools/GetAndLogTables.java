package adf.embers.tools;

import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.QueryDao;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import yatspec.renderers.ResultSetWrapper;

import javax.sql.DataSource;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.TABLE_QUERIES_STATISTICS;

public class GetAndLogTables {

    private final TestState testState;
    private final DataSource dataSource;

    public GetAndLogTables(TestState testState, DataSource dataSource) {
        this.testState = testState;
        this.dataSource = dataSource;
    }

    public StateExtractor<ResultSetWrapper> queriesTable() {
        return inputAndOutputs -> getAndLogRowsOnQueriesTable();
    }

    public ResultSetWrapper getAndLogRowsOnQueriesTable() {
        final String sql = "select * from " + QueryDao.TABLE_QUERIES
                + " order by " + QueryDao.COL_ID;
        ResultSetWrapper rowsOnQueriesTable = selectRows(sql);
        testState.log("Database - " + QueryDao.TABLE_QUERIES, rowsOnQueriesTable);
        return rowsOnQueriesTable;
    }

    public StateExtractor<ResultSetWrapper> queryStatisticsTable(String logKeyPrefix) {
        return inputAndOutputs -> getAndLogQueryStatistics(logKeyPrefix);
    }

    public ResultSetWrapper getAndLogQueryStatistics(String logKeyPrefix) {
        final String sql = "select * from " + TABLE_QUERIES_STATISTICS + " order by id desc";
        final ResultSetWrapper resultSetWrapper = selectRows(sql);
        testState.log(logKeyPrefix+TABLE_QUERIES_STATISTICS, resultSetWrapper);
        return resultSetWrapper;
    }


    public StateExtractor<ResultSetWrapper> queryResultCacheTable(String logKeyPrefix) {
        return inputAndOutputs -> getAndLogRowsOnQueryResultCacheTable(logKeyPrefix);
    }

    public ResultSetWrapper getAndLogRowsOnQueryResultCacheTable(String logKeyPrefix) {
        final String sql = "select * from " + QueryResultCacheDao.TABLE_QUERIES_RESULT_CACHE
                + " order by " + QueryResultCacheDao.COL_ID;
        final ResultSetWrapper resultSetWrapper = selectRows(sql);
        testState.log(logKeyPrefix + QueryResultCacheDao.TABLE_QUERIES_RESULT_CACHE, resultSetWrapper);
        return resultSetWrapper;
    }

    private ResultSetWrapper selectRows(String sql) {
        try (Handle handle = new DBI(dataSource).open()) {
            Query<Map<String, Object>> q = handle.createQuery(sql);
            return new ResultSetWrapper(q.list());
        }
    }
}
