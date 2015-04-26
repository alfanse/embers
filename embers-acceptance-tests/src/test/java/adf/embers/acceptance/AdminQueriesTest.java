package adf.embers.acceptance;

import adf.embers.add.AdminQueryHandler;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecHttpCommand;
import adf.embers.tools.YatspecHttpPostCommandBuilder;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.hamcrest.CoreMatchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("As an Authenticated admin user, I can change the available queries, so that reports can be added and existing ones amended/deleted.")
public class AdminQueriesTest extends TestState {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private YatspecHttpCommand http;

    @Test
    public void usersCanAddNewReports() throws Exception {
        when(aNewQueryIsPosted());
        then(http.responseCode(), is(HttpURLConnection.HTTP_OK));
        then(http.responseBody(), CoreMatchers.containsString("Successfully added query"));
    }

    private ActionUnderTest aNewQueryIsPosted() {
        http = new YatspecHttpPostCommandBuilder(this)
                .withUrl(embersServer.getFullContextPath() + AdminQueryHandler.PATH)
                .withName("newQuery")
                .withSql("select systimestamp from dual")
                .withDescription("a description")
                .build();
        return http.execute();
    }
}
