package adf.embers.acceptance;

import adf.embers.query.persistence.Query;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import org.junit.jupiter.api.Test;
import yatspec.http.YatspecHttpGetCommand;

import static adf.embers.tools.QueryInserter.ALL_QUERIES;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.CoreMatchers.*;

@Notes("As a user \n" +
        "I want to run sql reports against the database directly \n" +
        "So that I can mine the information I need \n" +
        "\n" +
        "This example hosts embers on Jetty, in a jersey ServletContainer.\n" +
        "IOC via ResourceConfig\n" +
        "Database via Hsqldb")
public class QueryTest extends EmbersAcceptanceTestBase {

    private final YatspecHttpGetCommand http = new YatspecHttpGetCommand(super.interactions);

    private final YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(super.interactions, embersServer.getEmbersDatabase().getDataSource());

    @Test
    public void queryNotFound() throws Exception {
        whenHttpGetRequestFor("unknownQuery");
        then(http.responseCode(), is(HTTP_NOT_FOUND));
        then(http.responseBody(), containsString("Query not found: unknownQuery"));
    }

    @Test
    public void queryRunsWithNoRowsOfData() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        whenHttpGetRequestFor("noRows");
        then(http.responseCode(), is(200));
        then(http.responseBody(), is(empty()));
    }

    @Test
    @Notes("Embers has a query to show all available queries")
    public void showAllAvailableQueries() throws Exception {
        givenEmbersHasAQueryThatReturnsNoRows();
        givenEmbersHasAQueryThatShowsAllQueries();
        whenHttpGetRequestFor(ALL_QUERIES);
        then(http.responseCode(), is(200));
        then(http.responseBody(), allOf(containsString("name"), containsString("description"), containsString("sql"), containsString(ALL_QUERIES)));
    }

    private void whenHttpGetRequestFor(String query) throws Exception {
        http.setUrl(embersServer.embersQueryPath() + "/" + query);
        http.execute();
    }

    private void givenEmbersHasAQueryThatShowsAllQueries() throws Exception {
        yatspecQueryInserter.allQueries();
    }

    private void givenEmbersHasAQueryThatReturnsNoRows() throws Exception {
        yatspecQueryInserter.insertQuery(new Query("noRows", "Show what happens when query runs but no data is selected", "select * from queries where name = 'missing'"));
    }

    private String empty() {
        return "";
    }
}
