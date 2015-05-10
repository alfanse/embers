package adf.embers.acceptance;

import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.Query;
import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import yatspec.http.YatspecHttpGetCommand;
import yatspec.renderers.HttpConnectionRenderer;
import yatspec.renderers.HttpUrlConnectionWrapper;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;

import static adf.embers.tools.QueryInserter.ALL_QUERIES;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpecRunner.class)
@Notes("As a user \n" +
        "I want to run sql reports against the database directly \n" +
        "So that I can mine the information I need \n" +
        "\n" +
        "This example hosts embers on Jetty, in a jersey ServletContainer.\n" +
        "IOC via ResourceConfig\n" +
        "Database via Hsqldb")
public class QueryExecutorHostedOnJettyTest extends TestState implements WithCustomResultListeners {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();

    private final YatspecHttpGetCommand http = new YatspecHttpGetCommand(this, embersServer.getFullContextPath() + "/" + QueryHandler.PATH);
    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());

    @Test
    public void queryNotFound() throws Exception {
        when(http.getRequestFor("unknownQuery"));
        then(http.responseCode(), is(HTTP_NOT_FOUND));
        then(http.responseBody(), containsString("Query not found: unknownQuery"));
    }

    @Test
    public void queryRunsWithNoRowsOfData() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        when(http.getRequestFor("noRows"));
        then(http.responseCode(), is(200));
        then(http.responseBody(), is(empty()));
    }

    @Test
    @Notes("Embers has a query to show all available queries")
    public void showAllAvailableQueries() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        givenEmbersHasAQueryThatShowsAllQueries();
        when(http.getRequestFor(ALL_QUERIES));
        then(http.responseCode(), is(200));
        then(http.responseBody(), allOf(containsString("name"), containsString("description"), containsString("sql"), containsString(ALL_QUERIES)));
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
