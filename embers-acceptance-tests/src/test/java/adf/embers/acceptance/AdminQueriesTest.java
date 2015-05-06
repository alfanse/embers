package adf.embers.acceptance;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecHttpCommand;
import adf.embers.tools.YatspecHttpGetCommand;
import adf.embers.tools.YatspecHttpPostCommandBuilder;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.hamcrest.CoreMatchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.StringMapper;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("As an Authenticated admin user, I can change the available queries, so that reports can be added and existing ones amended/deleted.")
public class AdminQueriesTest extends TestState {

    public static final String NEW_QUERY_NAME = "newQuery";
    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private YatspecHttpCommand httpPost;
    private YatspecHttpGetCommand httpGet;

    @Test
    public void usersCanAddNewReports() throws Exception {
        when(aNewQueryIsPosted());
        then(httpPost.responseCode(), is(HTTP_OK));
        then(httpPost.responseBody(), CoreMatchers.containsString("Successfully added query"));

        thenTheDatabaseHasTheQueryStoredWithoutEncryption();

        when(theNewQueryIsCalled());
        then(httpGet.responseCode(), is(HTTP_OK));
        then(httpGet.responseBody(), CoreMatchers.containsString("today,now"));
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

    private void thenTheDatabaseHasTheQueryStoredWithoutEncryption() {
        DataSource dataSource = embersServer.getEmbersDatabase().getDataSource();
        DBI dbi = new DBI(dataSource);
        try(Handle h = dbi.open()) {
            Query<Map<String, Object>> q = h.createQuery("select * from " + QueryDao.TABLE_QUERIES + " order by " + QueryDao.COL_ID);
            List<Map<String, Object>> rs = q.list();
            log("Database Queries", rs);
        }
    }
}
