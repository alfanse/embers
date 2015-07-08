package adf.embers.tools;

import adf.embers.acceptance.EmbersAcceptanceTestBase;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.QueryDao;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import yatspec.renderers.ResultSetWrapper;

import java.util.Map;

public class GetAndLogTables {

    private final TestState testState;

    public GetAndLogTables(TestState testState) {
        this.testState = testState;
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

    public ResultSetWrapper getAndLogRowsOnQueryResultCacheTable() {
        final String sql = "select * from " + QueryResultCacheDao.TABLE_QUERIES_RESULT_CACHE
                + " order by " + QueryResultCacheDao.COL_ID;
        final ResultSetWrapper resultSetWrapper = selectRows(sql);
        testState.log("Database - " + QueryResultCacheDao.TABLE_QUERIES_RESULT_CACHE, resultSetWrapper);
        return resultSetWrapper;
    }

    private ResultSetWrapper selectRows(String sql) {
        try (Handle handle = EmbersAcceptanceTestBase.embersServer.getEmbersDatabase().openDatabaseHandle()) {
            Query<Map<String, Object>> q = handle.createQuery(sql);
            return new ResultSetWrapper(q.list());
        }
    }
}
