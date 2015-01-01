package adf.embers.acceptance;

import adf.embers.acceptance.client.ActionUnderTestHttpCaller;
import adf.embers.query.QueryHandler;
import adf.embers.query.impl.QueryProcessor;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("Hosted on jetty, in a jersey ServletContainer, IOP via ResourceConfig")
public class QueryWithWsrsTest extends TestState {

    private Server server;

    @Before
    public void startHttpServer() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new QueryHandler(new QueryProcessor()));


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
    public void makeCall() throws Exception {
        when(userMakesHttpGetRequestFor("/queryNameHere"));
        then(theResponseCode(), is(200));
        then(theResponseBody(), containsString("queryNameHere"));
    }

    private StateExtractor<Integer> theResponseCode() {
        return inputAndOutput -> inputAndOutput.getType("Response Code", Integer.class);
    }

    private StateExtractor<String> theResponseBody() {
        return inputAndOutput -> inputAndOutput.getType("Response Body", String.class);
    }

    private ActionUnderTest userMakesHttpGetRequestFor(String queryToMake) throws IOException {
        return new ActionUnderTestHttpCaller(this).getRequestFor("http://localhost:8001/" + QueryHandler.PATH + queryToMake);
    }
}
