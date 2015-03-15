package adf.embers.acceptance;

import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecHttpCommand;
import adf.embers.tools.YatspecHttpPostCommandBuilder;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpecRunner.class)
@Notes("As an Authenticated admin user, I can change the available queries, so that reports can be added and existing ones amended/deleted.")
public class AdminQueriesTest extends TestState {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private YatspecHttpCommand http;

    @Test
    @Ignore("Under construction")
    public void usersCanAddNewReports() throws Exception {
        when(aNewQueryIsPosted());
//        then(http.responseCode(), is(HTTP_NOT_FOUND));
//        then(http.theErrorResponse(), containsString("Query not found: unknownQuery"));
    }

    private ActionUnderTest aNewQueryIsPosted() {
        http = new YatspecHttpPostCommandBuilder(this).withUrl(embersServer.getFullContextPath() + "/query/add").withName("newQuery").withSql("select systimestamp from dual").withDescription("a description").build();
        return http.execute();
    }
}
