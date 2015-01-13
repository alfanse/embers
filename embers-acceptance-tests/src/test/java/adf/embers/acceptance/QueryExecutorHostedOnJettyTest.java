package adf.embers.acceptance;

import adf.embers.EmbersDatabase;
import adf.embers.acceptance.client.ActionUnderTestHttpCaller;
import adf.embers.configuration.EmbersConfiguration;
import adf.embers.query.QueryHandler;
import adf.embers.query.persistence.Query;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("Hosted on jetty, in a jersey ServletContainer, IOP via ResourceConfig\n" +
        "Using Hsqldb as database")
public class QueryExecutorHostedOnJettyTest extends TestState {

    private static EmbersDatabase embersDatabase;
    private Server server;

    @BeforeClass
    public static void startDatabase() throws Exception {
        embersDatabase = new EmbersDatabase(EmbersDatabase.JDBC_URL);
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
    }

    @Before
    public void startHttpServer() throws Exception {
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(embersDatabase.getDataSource());
        QueryHandler queryHandler = embersConfiguration.getQueryHandler();

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(queryHandler);

        server = new Server(8001);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/");
        server.setHandler(handler);
        server.start();
    }

    @After
    public void stopHttpServer() throws Exception {
        server.stop();
    }

    @Test
    public void queryNotFound() throws Exception {
        when(userMakesHttpGetRequestFor("unknownQuery"));
        then(theResponseCode(), is(HTTP_NOT_FOUND));
        then(theErrorResponse(), containsString("Query not found: unknownQuery"));
    }

    @Test
    @Notes("Embers has a query to show all available queries")
    public void showAllAvailableQueries() throws Exception {
        givenEmbersHasAQueryThatShowsAllQueries();
        when(userMakesHttpGetRequestFor("allQueries"));
        then(theResponseCode(), is(200));
        then(theResponseBody(), CoreMatchers.allOf(containsString("name"), containsString("description"), containsString("sql"), containsString("allQueries")));
    }

    @Test
    public void queryRunsWithNoRowsOfData() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        when(userMakesHttpGetRequestFor("noRows"));
        then(theResponseCode(), is(200));
        then(theResponseBody(), is(""));
    }

    private void givenEmbersHasAQueryThatShowsAllQueries() {
        givenQuery(embersDatabase.getQueryAll());
    }

    private void givenEmbersHasAQueryThatReturnsNoRows() {
        givenQuery(new Query("noRows", "Show what happens when query runs but no data is selected", "select * from queries where name = 'missing'"));
    }

    private void givenQuery(Query noRows) {
        interestingGivens.add("Expected Query To Run", noRows);
        embersDatabase.insertQuery(noRows);
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

    private ActionUnderTest userMakesHttpGetRequestFor(String queryToMake) {
        return new ActionUnderTestHttpCaller(this).getRequestFor("http://localhost:8001/" + QueryHandler.PATH + "/" + queryToMake);
    }
}
