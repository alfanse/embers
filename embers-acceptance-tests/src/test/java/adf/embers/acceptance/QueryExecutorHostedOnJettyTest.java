package adf.embers.acceptance;

import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.Query;
import adf.embers.tools.ActionUnderTestHttpCaller;
import adf.embers.tools.EmbersQueries;
import adf.embers.tools.EmbersServer;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpecRunner.class)
@Notes("Hosted on jetty, in a jersey ServletContainer, IOC via ResourceConfig\n" +
        "Using Hsqldb as database")
public class QueryExecutorHostedOnJettyTest extends TestState {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();
    private EmbersQueries embersQueries = new EmbersQueries(this, embersServer.getEmbersDatabase());

    @Test
    public void queryNotFound() throws Exception {
        when(userMakesHttpGetRequestFor("unknownQuery"));
        then(theResponseCode(), is(HTTP_NOT_FOUND));
        then(theErrorResponse(), containsString("Query not found: unknownQuery"));
    }

    @Test
    public void queryRunsWithNoRowsOfData() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        when(userMakesHttpGetRequestFor("noRows"));
        then(theResponseCode(), is(200));
        then(theResponseBody(), is(empty()));
    }

    @Test
    @Notes("Embers has a query to show all available queries")
    public void showAllAvailableQueries() throws Exception {
        givenEmbersHasAQueryThatShowsAllQueries();
        when(userMakesHttpGetRequestFor("allQueries"));
        then(theResponseCode(), is(200));
        then(theResponseBody(), allOf(containsString("name"), containsString("description"), containsString("sql"), containsString("allQueries")));
    }

    private void givenEmbersHasAQueryThatShowsAllQueries() {
        embersQueries.insertAllQueries();
    }

    private void givenEmbersHasAQueryThatReturnsNoRows() {
        embersQueries.insertQuery(new Query("noRows", "Show what happens when query runs but no data is selected", "select * from queries where name = 'missing'"));
    }

    private ActionUnderTest userMakesHttpGetRequestFor(String queryToMake) {
        return new ActionUnderTestHttpCaller(this).getRequestFor("http://localhost:8001/" + QueryHandler.PATH + "/" + queryToMake);
    }

    private StateExtractor<Integer> theResponseCode() {
        return inputAndOutput -> inputAndOutput.getType("Response Code", Integer.class);
    }

    private StateExtractor<String> theErrorResponse() {
        return inputAndOutput -> inputAndOutput.getType("Error Response Body", String.class);
    }

    private StateExtractor<String> theResponseBody() {
        return inputAndOutput -> inputAndOutput.getType("Response Body", String.class);
    }

    private String empty() {
        return "";
    }
}
