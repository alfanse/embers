package adf.embers.acceptance;

import adf.embers.tools.GetAndLogTables;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import org.fest.assertions.data.MapEntry;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.renderers.ResultSetWrapper;

import java.util.List;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.*;
import static adf.embers.tools.QueryInserter.ALL_QUERIES;
import static org.fest.assertions.api.Assertions.assertThat;

@Notes("Performance of Embers Queries is audited to a database table.")
public class QueryStatisticsTest extends EmbersAcceptanceTestBase {

    private YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());
    private YatspecHttpGetCommand http = new YatspecHttpGetCommand(this);

    @Test
    public void auditAnExistingQueryThatRespondsWithData() throws Exception {
        given(yatspecQueryInserter.allQueries());
        when(httpGetRequestFor(ALL_QUERIES));
        then(thePerformanceAudit(), showsUsefulStatisticsAboutTheExecutedQuery());
    }

    private ActionUnderTest httpGetRequestFor(String query) {
        http.setUrl(embersServer.embersQueryPath() + "/" + query);
        return http;
    }

    private StateExtractor<ResultSetWrapper> thePerformanceAudit() {
        return new GetAndLogTables(this, embersServer.getEmbersDatabase().getDataSource()).getAndLogQueryStatistics("Database Table - " + TABLE_QUERIES_STATISTICS);
    }

    private TypeSafeDiagnosingMatcher<ResultSetWrapper> showsUsefulStatisticsAboutTheExecutedQuery() {
        return new TypeSafeDiagnosingMatcher<ResultSetWrapper>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Not the expected result.");
            }

            @Override
            protected boolean matchesSafely(ResultSetWrapper item, Description mismatchDescription) {
                final List<Map<String, Object>> resultSet = item.getResultSet();
                assertThat(resultSet).hasSize(1);
                final Map<String, Object> firstRow = resultSet.get(0);
                assertThat(firstRow).contains(MapEntry.entry(COL_QUERY_NAME, ALL_QUERIES));
                assertThat(firstRow).containsKey(COL_DATE_EXECUTED).containsKey(COL_DURATION).containsKey(COL_RESULT);
                assertThat(firstRow.get(COL_DATE_EXECUTED)).isNotNull();
                assertThat(firstRow.get(COL_DURATION)).isNotNull();
                assertThat(firstRow.get(COL_RESULT)).isNotNull();
                return true;
            }
        };
    }
}
