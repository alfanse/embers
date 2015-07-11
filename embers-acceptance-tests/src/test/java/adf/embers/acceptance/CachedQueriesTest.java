package adf.embers.acceptance;

import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.AssertionMatcher;
import adf.embers.tools.GetAndLogTables;
import adf.embers.tools.ResultSetWrapperMatcher;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.fest.assertions.core.Condition;
import org.fest.assertions.data.MapEntry;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import yatspec.http.YatspecHttpCommand;
import yatspec.http.YatspecHttpGetCommand;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.COL_DATE_EXECUTED;
import static adf.embers.query.persistence.QueryStatisticsDao.COL_QUERY_NAME;
import static com.googlecode.yatspec.matchers.Matchers.has;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

@Notes("As many users of a long running report" +
        "I want access speed to be really fast." +
        "I don't mind if the first user experiences a slow report, " +
        "I will trigger that first call from a timed job." +
        "I want the cached report to timeout on a frequency of my specification." +
        "The calling client needs to know its a cached report, but end user need not.")
public class CachedQueriesTest extends EmbersAcceptanceTestBase {

    private static final String NAME_OF_CACHED_QUERY = "cachedQuery";
    private static final String HEADER_KEY_REPORT_CACHED_AT = "REPORT_CACHED_AT";
    public static final String QUERY_COLUMN = "servertime";

    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());
    private final YatspecHttpCommand http = new YatspecHttpGetCommand(this);
    private final GetAndLogTables getAndLogTables = new GetAndLogTables(this, embersServer.getEmbersDatabase().getDataSource());
    private Date timeRequestMade;

    @Test
    public void aCachableQueryIsCalledWhenTheCacheIsEmpty() throws Exception {
        givenACacheableQuery();
        givenTheReportHasNotBeenCached();

        when(httpGetRequestForCacheableQuery());

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));
        then(http.responseHeaders(), has(entryCachedReportTrue()));

        thenQueryStasticsShowsTheQueryWasExecuted();
        thenQueryResultCacheShowsTheQueryHasJustBeenCached();
    }

    @Test
    @Ignore("Test in need of setup help")
    public void aCachedQueryIsUsedWhileStillWithinExpirationTime() throws Exception {
        givenAQueryHasRecentlyBeenMadeToACacheableQuery();

        when(httpGetRequestForCacheableQuery());

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));
        then(http.responseHeaders(), has(entryCachedReportTrue()));

        thenQueryStasticsShowsTheQueryWasNotExecuted();
        thenQueryResultCacheShowsTheQueryWasCachedBeforeTheQueryWasMade();
    }

    private void thenQueryResultCacheShowsTheQueryHasJustBeenCached() throws Exception {
        then(getAndLogTables.queryResultCacheTable("Database after - "),
                new ResultSetWrapperMatcher(resultSet -> {
                    assertThat(resultSet).hasSize(1);
                    final Map<String, Object> rowOfData = resultSet.get(0);
                    assertThat(rowOfData.get(QueryResultCacheDao.COL_QUERY_NAME)).isEqualTo(NAME_OF_CACHED_QUERY);
                    assertThat(rowOfData.get(QueryResultCacheDao.COL_DATE_CACHED))
                            .isNotNull().is(new Condition<Object>() {
                        @Override
                        public boolean matches(Object value) {
                            final long actualTime = ((Timestamp) value).getTime();
                            final long startTime = timeRequestMade.getTime();
                            assertThat(startTime).isLessThanOrEqualTo(actualTime);
                            return true;
                        }
                    });
                }));
    }

    private void thenQueryResultCacheShowsTheQueryWasCachedBeforeTheQueryWasMade() throws Exception {
        then(getAndLogTables.queryResultCacheTable("Database after - "),
                new ResultSetWrapperMatcher(resultSet -> {
                    assertThat(resultSet).hasSize(1);
                    final Map<String, Object> rowOfData = resultSet.get(0);
                    assertThat(rowOfData.get(QueryResultCacheDao.COL_QUERY_NAME)).isEqualTo(NAME_OF_CACHED_QUERY);
                    assertThat(rowOfData.get(QueryResultCacheDao.COL_DATE_CACHED))
                            .isNotNull().is(new Condition<Object>() {
                        @Override
                        public boolean matches(Object value) {
                            final long actualTime = ((Timestamp) value).getTime();
                            final long startTime = timeRequestMade.getTime();
                            assertThat(startTime).isGreaterThanOrEqualTo(actualTime);
                            return true;
                        }
                    });
                }));
    }

    private TestState thenQueryStasticsShowsTheQueryWasNotExecuted() throws Exception {
        return then(getAndLogTables.queryStatisticsTable("Database after - "),
                new ResultSetWrapperMatcher(assertionFunction -> {
                    assertThat(assertionFunction).hasSize(0);
                }));
    }

    private TestState thenQueryStasticsShowsTheQueryWasExecuted() throws Exception {
        return then(getAndLogTables.queryStatisticsTable("Database after - "),
                new ResultSetWrapperMatcher(resultSet -> {
                    assertThat(resultSet).hasSize(1);
                    final Map<String, Object> firstRow = resultSet.get(0);
                    assertThat(firstRow).contains(MapEntry.entry(COL_QUERY_NAME, NAME_OF_CACHED_QUERY));
                    assertThat(firstRow.get(COL_DATE_EXECUTED)).isNotNull();
                }));
    }

    private void givenAQueryHasRecentlyBeenMadeToACacheableQuery() throws Exception {
        givenACacheableQuery();

        //insert a query result into the cached table
        // make it a timestamp result from a few minutes ago.
    }

    private void givenACacheableQuery() throws Exception {
        yatspecQueryInserter.insertQuery(new Query(
                NAME_OF_CACHED_QUERY, "timestamp on database", "select current_timestamp as " + QUERY_COLUMN + " from " + QueryDao.TABLE_QUERIES, Duration.ofDays(1)))
                .build(interestingGivens);
        getAndLogTables.getAndLogRowsOnQueriesTable();
    }

    private void givenTheReportHasNotBeenCached() {
        getAndLogTables.getAndLogRowsOnQueryResultCacheTable("Database - ");
    }


    private ActionUnderTest httpGetRequestForCacheableQuery() {
        http.setUrl(embersServer.embersCachedQueryPath() + "/" + CachedQueriesTest.NAME_OF_CACHED_QUERY);
        timeRequestMade = new Date();
        return http;
    }

    private Matcher<Map<String, Object>> entryCachedReportTrue() {
        return new AssertionMatcher<>(item -> {
            assertThat(item).containsKey(HEADER_KEY_REPORT_CACHED_AT);
        });
    }

}
