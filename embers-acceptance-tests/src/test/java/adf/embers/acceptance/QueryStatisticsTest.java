package adf.embers.acceptance;

import adf.embers.query.QueryHandler;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import org.fest.assertions.data.MapEntry;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.renderers.ResultSetWrapper;

import java.util.List;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.*;
import static adf.embers.tools.QueryInserter.ALL_QUERIES;
import static org.fest.assertions.api.Assertions.assertThat;

@Notes("Performance of Embers Queries is audited to a database table.")
public class QueryStatisticsTest extends EmbersAcceptanceTestBase {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();

    private YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());
    private YatspecHttpGetCommand http = new YatspecHttpGetCommand(this, embersServer.getFullContextPath() + "/" + QueryHandler.PATH);

    @Test
    public void auditAnExistingQueryThatRespondsWithData() throws Exception {
        given(yatspecQueryInserter.allQueries());
        when(http.getRequestFor(ALL_QUERIES));
        then(thePerformanceAudit(), showsUsefulStatisticsAboutTheExecutedQuery());
    }

    private StateExtractor<List<Map<String, Object>>> thePerformanceAudit() {
        return inputAndOutputs -> {
            Handle handle = new DBI(embersServer.getEmbersDatabase().getDataSource()).open();
            final List<Map<String, Object>> result = handle.select("select * from " + TABLE_QUERIES_STATISTICS + " order by id desc");
            log("Database Table - "+TABLE_QUERIES_STATISTICS, new ResultSetWrapper(result));
            handle.close();
            return result;
        };
    }

    private TypeSafeDiagnosingMatcher<List<Map<String, Object>>> showsUsefulStatisticsAboutTheExecutedQuery() {
        return new TypeSafeDiagnosingMatcher<List<Map<String, Object>>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Not the expected result.");
            }

            @Override
            protected boolean matchesSafely(List<Map<String, Object>> item, Description mismatchDescription) {
                assertThat(item).hasSize(1);
                final Map<String, Object> firstRow = item.get(0);
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
