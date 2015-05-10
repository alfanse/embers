package adf.embers.acceptance;

import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.Query;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import yatspec.http.YatspecHttpGetCommand;

import static adf.embers.tools.QueryInserter.ALL_QUERIES;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpecRunner.class)
@Notes("Embers is a Embedded sql reporting tool for running predetermined sql queries." +
        "This example hosts embers on Jetty, in a jersey ServletContainer.\n" +
        "IOC via ResourceConfig\n" +
        "Database via Hsqldb")
public class QueryExecutorHostedOnJettyTest extends TestState {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();

    private final YatspecHttpGetCommand httpGet = new YatspecHttpGetCommand(this, embersServer.getFullContextPath() + "/" + QueryHandler.PATH);
    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());

    @Test
    public void queryNotFound() throws Exception {
        when(httpGet.getRequestFor("unknownQuery"));
        then(httpGet.responseCode(), is(HTTP_NOT_FOUND));
        then(httpGet.responseBody(), containsString("Query not found: unknownQuery"));
    }

    @Test
    public void queryRunsWithNoRowsOfData() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        when(httpGet.getRequestFor("noRows"));
        then(httpGet.responseCode(), is(200));
        then(httpGet.responseBody(), is(empty()));
    }

    @Test
    @Notes("Embers has a query to show all available queries")
    public void showAllAvailableQueries() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        givenEmbersHasAQueryThatShowsAllQueries();
        when(httpGet.getRequestFor(ALL_QUERIES));
        then(httpGet.responseCode(), is(200));
        then(httpGet.responseBody(), allOf(containsString("name"), containsString("description"), containsString("sql"), containsString(ALL_QUERIES)));
    }

    private void givenEmbersHasAQueryThatShowsAllQueries() throws Exception {
        given(yatspecQueryInserter.allQueries());
    }

    private void givenEmbersHasAQueryThatReturnsNoRows() throws Exception {
        given(yatspecQueryInserter.insertQuery(new Query("noRows", "Show what happens when query runs but no data is selected", "select * from queries where name = 'missing'")));
    }

    private String empty() {
        return "";
    }
}
