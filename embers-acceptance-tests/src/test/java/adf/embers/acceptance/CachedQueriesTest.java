package adf.embers.acceptance;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.GetAndLogTables;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import org.fest.assertions.data.MapEntry;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import yatspec.http.YatspecHttpCommand;
import yatspec.http.YatspecHttpGetCommand;

import java.time.Duration;
import java.util.Map;

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

    @Test
    public void aCachableQueryIsCalledWhenTheCacheIsEmpty() throws Exception {
        givenACacheableQuery();
        givenTheReportHasNotBeenCached();

        when(httpGetRequestForCacheableQuery());

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));

        //noinspection unchecked
        //todo cached date into header
//        then(http.responseHeaders(), has(entryCachedReportTrue()));

    }

    @Test
    public void aCachedQueryIsUsedWhileStillWithinExpirationTime() throws Exception {
        givenAQueryHasRecentlyBeenMadeToACacheableQuery();

        when(httpGetRequestForCacheableQuery());

        then(http.responseCode(), is(200));
        then(http.responseBody(), startsWith(QUERY_COLUMN));

        //noinspection unchecked
        //todo cached date into header
//        then(http.responseHeaders(), has(entryCachedReportTrue()));
    }


    //cacheMiss_cachesResultWithFreshTimestamp

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
        getAndLogTables.getAndLogRowsOnQueryResultCacheTable();
    }


    private ActionUnderTest httpGetRequestForCacheableQuery() {
        http.setUrl(embersServer.embersCachedQueryPath() + "/" + CachedQueriesTest.NAME_OF_CACHED_QUERY);
        return http;
    }

    private Matcher<Map<String, Object>> entryCachedReportTrue() {
        return new TypeSafeMatcher<Map<String, Object>>() {
            @Override
            protected boolean matchesSafely(Map<String, Object> item) {
                assertThat(item).contains(MapEntry.entry(HEADER_KEY_REPORT_CACHED_AT, Boolean.TRUE));
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected headers map to contain key/value: ").appendValue(HEADER_KEY_REPORT_CACHED_AT);
            }
        };
    }

}
