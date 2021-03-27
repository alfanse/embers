package adf.embers.acceptance;

import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.GetAndLogTables;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import org.fest.assertions.core.Condition;
import org.fest.assertions.data.MapEntry;
import org.junit.jupiter.api.Test;
import yatspec.http.YatspecHttpCommand;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.renderers.ResultSetWrapper;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static adf.embers.query.persistence.QueryStatisticsDao.COL_DATE_EXECUTED;
import static adf.embers.query.persistence.QueryStatisticsDao.COL_QUERY_NAME;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Notes("As many users of a long running report<br/>" +
        "I want access speed to be really fast.<br/>" +
        "I don't mind if the first user experiences a slow report, <br/>" +
        "I will trigger that first call from a timed job.<br/>" +
        "I want the cached report to timeout on a frequency of my specification.<br/>" +
        "The calling client needs to know its a cached report, but end user need not.")
public class CachedQueriesTest extends EmbersAcceptanceTestBase {

    private static final String NAME_OF_CACHED_QUERY = "cachedQuery";
    private static final String HEADER_KEY_REPORT_CACHED_AT = "REPORT_CACHED_AT";
    private static final String QUERY_COLUMN = "firstcolumn";
    private static final Duration CACHE_DURATION_1_DAY = Duration.ofDays(1);

    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(super.interactions, embersServer.getEmbersDatabase().getDataSource());
    private final YatspecHttpCommand http = new YatspecHttpGetCommand(super.interactions);
    private final GetAndLogTables getAndLogTables = new GetAndLogTables(super.interactions, embersServer.getEmbersDatabase().getDataSource());
    private long timeRequestMade;

    @SuppressWarnings("unchecked")
    @Test
    public void aCachableQueryIsCalledWhenTheCacheIsEmpty() throws Exception {
        givenACacheableQuery();
        givenTheReportHasNotBeenCached();

        whenHttpGetRequestForCacheableQuery();

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));
        then(http.responseHeaders(), hasKey(HEADER_KEY_REPORT_CACHED_AT));

        thenQueryStaticsShowsTheQueryWasExecuted();
        thenQueryResultCacheShowsTheQueryHasJustBeenCached();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aCachedQueryIsUsedWhileStillWithinExpirationTime() throws Exception {
        givenACacheableQuery();
        //todo fix test by setting CachedQuery.result to not null
        givenTheReportHasRecentlyBeenCached();

        IntStream.of(3).forEach(i-> {
            try {
                whenHttpGetRequestForCacheableQuery();
                then(http.responseCode(), is(200));
                then(http.responseBody(), startsWith(QUERY_COLUMN));
                then(http.responseHeaders(), hasKey(HEADER_KEY_REPORT_CACHED_AT));
                thenQueryResultCacheShowsTheQueryWasCachedBeforeTheQueryWasMade();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        thenQueryStaticsShowsTheQueryWasNotExecuted();
    }

    private void thenQueryResultCacheShowsTheQueryHasJustBeenCached() throws Exception {
        ResultSetWrapper resultSetWrapper = getAndLogTables.queryResultCacheTable("Database after - ");
        List<Map<String, Object>> resultSet = resultSetWrapper.getResultSet();
        assertThat(resultSet).hasSize(1);

        final Map<String, Object> rowOfData = resultSet.get(0);
        assertThat(rowOfData.get(QueryResultCacheDao.COL_QUERY_NAME)).isEqualTo(NAME_OF_CACHED_QUERY);
        assertThat(rowOfData.get(QueryResultCacheDao.COL_DATE_CACHED)).isNotNull()
                .is(new Condition<Object>() {
                        @Override
                        public boolean matches(Object value) {
                    final long actualTime = ((Timestamp) value).getTime();
                    final long startTime = timeRequestMade;
                    assertThat(startTime).isLessThanOrEqualTo(actualTime);
                    return true;
                    }
                });
    }

    private void thenQueryResultCacheShowsTheQueryWasCachedBeforeTheQueryWasMade() throws Exception {
        ResultSetWrapper resultSetWrapper = getAndLogTables.queryResultCacheTable("Database after - ");
        List<Map<String, Object>> resultSet = resultSetWrapper.getResultSet();
        assertThat(resultSet).hasSize(1);
        final Map<String, Object> rowOfData = resultSet.get(0);
        assertThat(rowOfData.get(QueryResultCacheDao.COL_QUERY_NAME)).isEqualTo(NAME_OF_CACHED_QUERY);
        assertThat(rowOfData.get(QueryResultCacheDao.COL_DATE_CACHED))
            .isNotNull()
            .is(new Condition<Object>() {
                        @Override
                        public boolean matches(Object value) {
                final long timeResultCached = ((Timestamp) value).getTime();
                assertThat(timeRequestMade).isGreaterThanOrEqualTo(timeResultCached);
                return true;
            }
        });
    }

    private void thenQueryStaticsShowsTheQueryWasNotExecuted() throws Exception {
        ResultSetWrapper resultSetWrapper = getAndLogTables.queryStatisticsTable("Database after - ");
        List<Map<String, Object>> resultSet = resultSetWrapper.getResultSet();
        assertThat(resultSet).hasSize(1);
    }

    private void thenQueryStaticsShowsTheQueryWasExecuted() throws Exception {
        ResultSetWrapper resultSetWrapper = getAndLogTables.queryStatisticsTable("Database after - ");
        List<Map<String, Object>> resultSet = resultSetWrapper.getResultSet();
        assertThat(resultSet).hasSize(1);
        final Map<String, Object> firstRow = resultSet.get(0);
        assertThat(firstRow).contains(MapEntry.entry(COL_QUERY_NAME, NAME_OF_CACHED_QUERY));
        assertThat(firstRow.get(COL_DATE_EXECUTED)).isNotNull();
    }

    private void givenACacheableQuery() {
        yatspecQueryInserter.insertQuery(
            new Query(
                NAME_OF_CACHED_QUERY,
                "get 'something' from database",
                "select 'something' as " + QUERY_COLUMN + " from " + QueryDao.TABLE_QUERIES,
                CACHE_DURATION_1_DAY));
        getAndLogTables.getAndLogRowsOnQueriesTable();
    }

    private void givenTheReportHasRecentlyBeenCached() throws Exception {
        whenHttpGetRequestForCacheableQuery();

        getAndLogTables.getAndLogRowsOnQueryResultCacheTable("Database Before - ");
    }

    private void givenTheReportHasNotBeenCached() {
        getAndLogTables.getAndLogRowsOnQueryResultCacheTable("Database - ");
    }

    private void whenHttpGetRequestForCacheableQuery() throws Exception {
        timeRequestMade = System.currentTimeMillis();
        http.setUrl(embersServer.embersCachedQueryPath() + "/" + CachedQueriesTest.NAME_OF_CACHED_QUERY);
        http.execute();
    }

}
