package adf.embers.acceptance;

import adf.embers.cache.persistence.CachedQuery;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.AssertionMatcher;
import adf.embers.tools.GetAndLogTables;
import adf.embers.tools.ResultSetWrapperMatcher;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import org.fest.assertions.core.Condition;
import org.fest.assertions.data.MapEntry;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import yatspec.http.YatspecHttpCommand;
import yatspec.http.YatspecHttpGetCommand;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private static final String QUERY_COLUMN = "servertime";
    private static final Duration CACHE_DURATION_1_DAY = Duration.ofDays(1);

    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());
    private final YatspecHttpCommand http = new YatspecHttpGetCommand(this);
    private final GetAndLogTables getAndLogTables = new GetAndLogTables(this, embersServer.getEmbersDatabase().getDataSource());
    private Date timeRequestMade;

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @Test
    @Ignore("Test in need of setup help")
    public void aCachedQueryIsUsedWhileStillWithinExpirationTime() throws Exception {
        givenACacheableQuery();
        givenTheReportHasRecentlyBeenCached();

        when(httpGetRequestForCacheableQuery());

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));
        then(http.responseHeaders(), has(entryCachedReportTrue()));

        thenQueryResultCacheShowsTheQueryWasCachedBeforeTheQueryWasMade();
        thenQueryStasticsShowsTheQueryWasNotExecuted();
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
                            .isNotNull()
                            .is(new Condition<Object>() {
                        @Override
                        public boolean matches(Object value) {
                            final long timeResultCached = ((Timestamp) value).getTime();
                            final long httpRequestTime = timeRequestMade.getTime();
                            assertThat(httpRequestTime).isGreaterThanOrEqualTo(timeResultCached);
                            return true;
                        }
                    });
                }));
    }

    private void thenQueryStasticsShowsTheQueryWasNotExecuted() throws Exception {
        then(getAndLogTables.queryStatisticsTable("Database after - "),
                new ResultSetWrapperMatcher(assertionFunction -> assertThat(assertionFunction).hasSize(0)));
    }

    private void thenQueryStasticsShowsTheQueryWasExecuted() throws Exception {
        then(getAndLogTables.queryStatisticsTable("Database after - "),
                new ResultSetWrapperMatcher(resultSet -> {
                    assertThat(resultSet).hasSize(1);
                    final Map<String, Object> firstRow = resultSet.get(0);
                    assertThat(firstRow).contains(MapEntry.entry(COL_QUERY_NAME, NAME_OF_CACHED_QUERY));
                    assertThat(firstRow.get(COL_DATE_EXECUTED)).isNotNull();
                }));
    }

    private void givenACacheableQuery() throws Exception {
        yatspecQueryInserter.insertQuery(new Query(
                NAME_OF_CACHED_QUERY, "timestamp on database", "select current_timestamp as " + QUERY_COLUMN + " from " + QueryDao.TABLE_QUERIES, CACHE_DURATION_1_DAY))
                .build(interestingGivens);
        getAndLogTables.getAndLogRowsOnQueriesTable();
    }

    private void givenTheReportHasRecentlyBeenCached() {
        DBI dbi = new DBI(embersServer.getEmbersDatabase().getDataSource());
        QueryResultCacheDao cacheDao = dbi.open(QueryResultCacheDao.class);
        final CachedQuery cachedQuery = new CachedQuery(NAME_OF_CACHED_QUERY, CACHE_DURATION_1_DAY.toMillis());
        cacheDao.save(cachedQuery);

        cachedQuery.setCachedQueryResult(new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("heading", "value");
            }});
        }});
        cachedQuery.setDateWhenCached(new Date(System.currentTimeMillis() - 3600 * 1000));
        cacheDao.updateQueryCacheResult(cachedQuery);
        dbi.close(cacheDao);

        getAndLogTables.getAndLogRowsOnQueryResultCacheTable("Database Before - ");
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
        return new AssertionMatcher<>(item -> assertThat(item).containsKey(HEADER_KEY_REPORT_CACHED_AT));
    }

}
