package adf.embers.acceptance;

import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.YatspecHttpPostCommandBuilder;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import yatspec.http.YatspecHttpDeleteCommand;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.http.YatspecHttpPostCommand;
import yatspec.renderers.ResultSetWrapper;

import java.util.List;
import java.util.Map;

import static adf.embers.statics.UrlTools.encodeString;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@Notes("As an admin user<br/>" +
        "I can: add, update and delete queries<br/>" +
        "So that users can instantly use the changed query.")
public class AdminQueriesTest extends EmbersAcceptanceTestBase {

    /*This query Name has a space in it to force the need for encoding*/
    public static final String QUERY_NAME = "new Query";
    public static final String ADDED_SQL = "SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))";
    public static final String ADDED_DESC = "this query returns the date,time";
    public static final String UPDATED_SQL = "SELECT CURRENT_DATE AS date, CURRENT_TIME AS time FROM (VALUES(0))";
    public static final String UPDATED_DESC = "this query returns the date and time";

    private YatspecHttpPostCommand httpPost;
    private YatspecHttpGetCommand httpGet;
    private YatspecHttpDeleteCommand httpDelete;
    private String postedSql;
    private String postedDescription;

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
        httpDelete = new YatspecHttpDeleteCommand(this);
        httpDelete.setLogPrefix("Delete Query - ");
        httpDelete.setUrl(embersServer.embersAdminPath() + "/" + encodeString(QUERY_NAME));
        return httpDelete;
    }

    private ActionUnderTest anUpdateToTheQueryIsPosted() {
        postedSql = UPDATED_SQL;
        postedDescription = UPDATED_DESC;
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersServer.embersAdminPath())
                .withQueryName(QUERY_NAME)
                .withQuerySql(postedSql)
                .withQueryDescription(postedDescription)
                .build();
        httpPost.setLogPrefix("Update Query - ");
        return httpPost;

    }

    private void givenAnExistingReport() throws Exception {
        when(aNewQueryIsCreated());
        httpPost = null;
    }

    private ActionUnderTest aNewQueryIsCreated() {
        postedSql = ADDED_SQL;
        postedDescription = ADDED_DESC;
        httpPost = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersServer.embersAdminPath())
                .withQueryName(QUERY_NAME)
                .withQuerySql(postedSql)
                .withQueryDescription(postedDescription)
                .build();
        httpPost.setLogPrefix("Create Query - ");
        return httpPost;
    }

    private ActionUnderTest theNewQueryIsCalled() {
        this.httpGet = new YatspecHttpGetCommand(this);
        httpGet.setLogPrefix("Run Query - ");
        httpGet.setUrl(embersServer.embersQueryPath() + "/" + encodeString(QUERY_NAME));
        return httpGet;
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
            public void describeTo(Description description) {
            }

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
            public void describeTo(Description description) {
            }

            @Override
            public boolean matches(Object item) {
                List<Map<String, Object>> resultSet = ((ResultSetWrapper) item).getResultSet();
                assertThat(resultSet).hasSize(0);
                return true;
            }
        };
    }

}
