package adf.embers.acceptance;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryHandler;
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

//        then(theDatabaseHasTheQueryStoredWithoutEncryption());

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
}
