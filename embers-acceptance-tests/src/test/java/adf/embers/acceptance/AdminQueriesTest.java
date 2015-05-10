package adf.embers.acceptance;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.EmbersServer;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import yatspec.http.YatspecHttpDeleteCommand;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.http.YatspecHttpPostCommand;
import yatspec.http.YatspecHttpPostCommandBuilder;
import yatspec.renderers.HttpConnectionRenderer;
import yatspec.renderers.HttpUrlConnectionWrapper;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("As an admin user<br/>" +
        "I can: add, update and delete queries<br/>" +
        "So that users can instantly use the changed query.")
public class AdminQueriesTest extends TestState implements WithCustomResultListeners {

    /*This query Name has a space in it to force the need for encoding*/
    public static final String QUERY_NAME = "new Query";
    public static final String ADDED_SQL = "SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))";
    public static final String ADDED_DESC = "this query returns the date,time";
    public static final String UPDATED_SQL = "SELECT CURRENT_DATE AS date, CURRENT_TIME AS time FROM (VALUES(0))";
    public static final String UPDATED_DESC = "this query returns the date and time";

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private YatspecHttpPostCommand httpPost;
    private YatspecHttpGetCommand httpGet;
    private YatspecHttpDeleteCommand httpDelete;
    private String postedSql;
    private String postedDescription;

    @Before
    public void clearDatabase(){
        embersServer.getEmbersDatabase().clearQueries();
    }

    @Test
    public void usersCanAddNewReports() throws Exception {
        when(aNewQueryIsCreated());
        then(httpPost.responseCode(), is(HTTP_OK));
        then(httpPost.responseBody(), CoreMatchers.containsString("Successfully added query"));

        then(theQueriesTable(), hasTheQuery());

        when(theNewQueryIsCalled());
        then(httpGet.responseCode(), is(HTTP_OK));
        then(httpGet.responseBody(), CoreMatchers.containsString("today,now"));
    }

    @Test
    public void usersCanUpdateExistingReports() throws Exception {
        givenAnExistingReport();

        when(anUpdateToTheQueryIsPosted());
        then(httpPost.responseCode(), is(HTTP_OK));
        then(httpPost.responseBody(), CoreMatchers.containsString("Successfully updated query"));

        then(theQueriesTable(), hasTheQuery());
    }

    @Test
    public void usersCanDeleteExistingReports() throws Exception {
        givenAnExistingReport();
        then(theQueriesTable(), hasTheQuery());

        when(theQueryIsDeleted());
        then(httpDelete.responseCode(), is(HTTP_OK));
        then(httpDelete.responseBody(), CoreMatchers.containsString("Successfully deleted query"));

        then(theQueriesTable(), isMissingTheQuery());
    }

    private ActionUnderTest theQueryIsDeleted() {
        httpDelete = new YatspecHttpDeleteCommand(this, embersAdminPath());
        httpDelete.setLogPrefix("Delete Query - ");
        httpDelete.deleteRequestFor(QUERY_NAME);
        return httpDelete.execute();
    }

    private ActionUnderTest anUpdateToTheQueryIsPosted() {
        postedSql = UPDATED_SQL;
        postedDescription = UPDATED_DESC;
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersAdminPath())
                .withName(QUERY_NAME)
                .withSql(postedSql)
                .withDescription(postedDescription)
                .build();
        httpPost.setLogPrefix("Update Query - ");
        return httpPost.execute();

    }

    private void givenAnExistingReport() throws Exception {
        when(aNewQueryIsCreated());
        httpPost = null;
    }

    private ActionUnderTest aNewQueryIsCreated() {
        postedSql = ADDED_SQL;
        postedDescription = ADDED_DESC;
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersAdminPath())
                .withName(QUERY_NAME)
                .withSql(postedSql)
                .withDescription(postedDescription)
                .build();
        httpPost.setLogPrefix("Create Query - ");
        return httpPost.execute();
    }

    private ActionUnderTest theNewQueryIsCalled() {
        this.httpGet = new YatspecHttpGetCommand(this, embersServer.getFullContextPath() + "/" + QueryHandler.PATH);
        httpGet.setLogPrefix("Run Query - ");
        return httpGet.getRequestFor(QUERY_NAME);
    }

    private StateExtractor<ResultSetWrapper> theQueriesTable() {
        return inputAndOutputs -> {
            try (Handle handle = embersServer.getEmbersDatabase().openDatabaseHandle()) {
                Query<Map<String, Object>> q = handle.createQuery("select * from " + QueryDao.TABLE_QUERIES + " order by " + QueryDao.COL_ID);
                ResultSetWrapper resultSetWrapper = new ResultSetWrapper(q.list());
                log("Database - " + QueryDao.TABLE_QUERIES, resultSetWrapper);
                return resultSetWrapper;
            }
        };
    }

    private BaseMatcher<ResultSetWrapper> hasTheQuery() {
        return new BaseMatcher<ResultSetWrapper>() {

            @Override
            public void describeTo(Description description) { }

            @Override
            public boolean matches(Object item) {
                List<Map<String, Object>> resultSet = ((ResultSetWrapper) item).getResultSet();
                assertThat(resultSet).hasSize(1);
                assertThat(resultSet.get(0).get(QueryDao.COL_SQL)).isEqualTo(postedSql);
                assertThat(resultSet.get(0).get(QueryDao.COL_DESCRIPTION)).isEqualTo(postedDescription);
                return true;
            }
        };
    }

    private org.hamcrest.Matcher<ResultSetWrapper> isMissingTheQuery() {
        return new BaseMatcher<ResultSetWrapper>() {

            @Override
            public void describeTo(Description description) { }

            @Override
            public boolean matches(Object item) {
                List<Map<String, Object>> resultSet = ((ResultSetWrapper) item).getResultSet();
                assertThat(resultSet).hasSize(0);
                return true;
            }
        };
    }

    private String embersAdminPath() {
        return embersServer.getFullContextPath()+ AdminQueryHandler.PATH;
    }

    @Override
    public Iterable<SpecResultListener> getResultListeners() throws Exception {
        ArrayList<SpecResultListener> specResultListeners = new ArrayList<>();
        specResultListeners.add(
                new HtmlResultRenderer()
                        .withCustomRenderer(ResultSetWrapper.class, new ResultSetRenderer())
                        .withCustomRenderer(HttpUrlConnectionWrapper.class, new HttpConnectionRenderer())
        );
        return specResultListeners;
    }
}
