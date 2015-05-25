package adf.embers.tools.functions;

import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import yatspec.renderers.ResultSetWrapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.TABLE_QUERIES_STATISTICS;

public class QueryFunctions {
    public static StateExtractor<List<Map<String, Object>>> getAndLogQueryStatistics(DataSource dataSource, TestState testState) {
        return inputAndOutputs -> {
            Handle handle = new DBI(dataSource).open();
            final List<Map<String, Object>> result = handle.select("select * from " + TABLE_QUERIES_STATISTICS + " order by id desc");
            testState.log("Database Table - " + TABLE_QUERIES_STATISTICS, new ResultSetWrapper(result));
            handle.close();
            return result;
        };
    }
}
