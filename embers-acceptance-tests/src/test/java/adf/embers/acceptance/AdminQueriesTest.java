package adf.embers.acceptance;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecHttpCommand;
import adf.embers.tools.YatspecHttpGetCommand;
import adf.embers.tools.YatspecHttpPostCommandBuilder;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("As an Authenticated admin user, I can change the available queries, so that reports can be added and existing ones amended/deleted.")
public class AdminQueriesTest extends TestState implements WithCustomResultListeners {

    public static final String NEW_QUERY_NAME = "newQuery";
    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private YatspecHttpCommand httpPost;
    private YatspecHttpGetCommand httpGet;

    @Before
    public void clearDatabase(){
        try (Handle handle = embersServer.openHandleToEmbersDatabase()) {
            handle.execute("delete from "+QueryDao.TABLE_QUERIES);
        }
    }

    @Test
    public void usersCanAddNewReports() throws Exception {
        when(aNewQueryIsPosted());
        then(httpPost.responseCode(), is(HTTP_OK));
        then(httpPost.responseBody(), CoreMatchers.containsString("Successfully added query"));

        then(theQueriesTable(), hasTheQuery());

        when(theNewQueryIsCalled());
        then(httpGet.responseCode(), is(HTTP_OK));
        then(httpGet.responseBody(), CoreMatchers.containsString("today,now"));
    }

//    @Test
    @Ignore //TODO implement this
    public void usersCanUpdateExistingReports() throws Exception {
        givenAnExistingReport();

        when(anUpdateToTheQueryIsPosted());
        then(httpPost.responseCode(), is(HTTP_OK));
        then(httpPost.responseBody(), CoreMatchers.containsString("Successfully added query"));

        then(theQueriesTable(), hasTheQuery());
    }

    private ActionUnderTest anUpdateToTheQueryIsPosted() {
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersServer.getFullContextPath() + AdminQueryHandler.PATH)
                .withName(NEW_QUERY_NAME)
                .withSql("SELECT CURRENT_DATE AS now, CURRENT_TIME AS now FROM (VALUES(0))")
                .withDescription("this query returns the date,time as now")
                .build();
        return httpPost.execute();

    }

    private void givenAnExistingReport() throws Exception {
        when(aNewQueryIsPosted());
    }

    private ActionUnderTest aNewQueryIsPosted() {
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersServer.getFullContextPath() + AdminQueryHandler.PATH)
                .withName(NEW_QUERY_NAME)
                .withSql("SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))")
                .withDescription("this query returns the date,time")
                .build();
        return httpPost.execute();
    }

    private ActionUnderTest theNewQueryIsCalled() {
        this.httpGet = new YatspecHttpGetCommand(this, embersServer.getFullContextPath() + "/" + QueryHandler.PATH);
        httpGet.setLogPrefix("Second");
        return httpGet.getRequestFor(NEW_QUERY_NAME);
    }

    private BaseMatcher<ResultSetWrapper> hasTheQuery() {
        return new BaseMatcher<ResultSetWrapper>() {

            @Override
            public void describeTo(Description description) { }

            @Override
            public boolean matches(Object item) {
                List<Map<String, Object>> resultSet = ((ResultSetWrapper) item).getResultSet();
                assertThat(resultSet).hasSize(1);
                return true;
            }
        };
    }

    private StateExtractor<ResultSetWrapper> theQueriesTable() {
        return inputAndOutputs -> {
            try (Handle handle = embersServer.openHandleToEmbersDatabase()) {
                Query<Map<String, Object>> q = handle.createQuery("select * from " + QueryDao.TABLE_QUERIES + " order by " + QueryDao.COL_ID);
                ResultSetWrapper resultSetWrapper = new ResultSetWrapper(q.list());
                log("Database - " + QueryDao.TABLE_QUERIES, resultSetWrapper);
                return resultSetWrapper;
            }
        };
    }

    @Override
    public Iterable<SpecResultListener> getResultListeners() throws Exception {
        ArrayList<SpecResultListener> specResultListeners = new ArrayList<>();
        specResultListeners.add(
                new HtmlResultRenderer().withCustomRenderer(ResultSetWrapper.class, new ResultSetRenderer()));
        return specResultListeners;
    }
}
