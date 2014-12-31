package adf.embers.acceptance;

import adf.embers.query.QueryHandler;
import adf.embers.query.impl.QueryProcessor;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.hamcrest.CustomTypeSafeMatcher;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;

import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpecRunner.class)
@Notes("Hosted on com.sun.net.httpserver.HttpServer")
public class QueryApiTest extends TestState {

    public static final String CONTEXT_PATH = "embers";
    public static final int PORT = 9000;
    private HttpServer server;

    @Before
    public void startHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/" + CONTEXT_PATH, embersHandler());
        Executor useDefaultExecutor = null;
        server.setExecutor(useDefaultExecutor);
        server.start();
    }

    @After
    public void stopHttpServer(){
        server.stop(1);
    }

    @Test
    @Ignore("In development")
    public void queryMissing() throws Exception {
        when(userMakesHttpGetRequestFor("/missingQuery"));

        then(theResponseCode(), isExpectedToBe(HTTP_NOT_IMPLEMENTED));
        then(theResponseBody(), containsString("Query not found: missingQuery"));
    }

    @Test
    public void queryValidWithNoParameters() throws Exception {
        when(userMakesHttpGetRequestFor("/validQuery"));

        then(theResponseCode(), isExpectedToBe(HTTP_OK));
        then(theResponseBody(), containsString("validQuery"));
    }

    private ActionUnderTest userMakesHttpGetRequestFor(String queryToMake) throws IOException {
        return (givens, capturedInputAndOutputs) -> {
            URL u = new URL("http://localhost:"+PORT+ "/" + CONTEXT_PATH + queryToMake);
            givens.add("Url", u.toExternalForm());

            URLConnection conn = u.openConnection();
            InputStream inputStream = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            try( BufferedReader br = new BufferedReader((new InputStreamReader(inputStream)))) {
                for ( String line; (line = br.readLine()) != null; ) {
                    sb.append(line);
                }
            }
            log("Response Body", sb.toString());
            log("Response Content-Type", conn.getContentType());
            log("Response Content Length", conn.getContentLengthLong());
            log("Response Code", ((HttpURLConnection )conn).getResponseCode());

            //todo httpUrlConnection renderer
            log("HttpUrlConnection", (HttpURLConnection) conn);
            ((HttpURLConnection) conn).disconnect();
            return capturedInputAndOutputs;
        };
    }

    private StateExtractor<Integer> theResponseCode() {
        return inputAndOutputs -> inputAndOutputs.getType("Response Code", Integer.class);
    }

    private StateExtractor<String> theResponseBody() {
        return inputAndOutput-> inputAndOutput.getType("Response Body", String.class);
    }

    private Matcher<Integer> isExpectedToBe(int expectedHttpResponseCode) {
        interestingGivens.add("Expected Response Code", expectedHttpResponseCode);
        return is(expectedHttpResponseCode);
    }

    private HttpHandler embersHandler() {
        return httpExchange -> {

            URI requestURI = httpExchange.getRequestURI();
            String requestMethod = requestURI.getPath();
            String[] split = requestMethod.split("/");
            int length = split.length;

            Response response1 = new QueryHandler(new QueryProcessor()).executeQuery(split[length - 1]);

            String response = response1.getEntity().toString();
            String contentType = "text/plain";
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type", contentType);
            byte[] responseBytes = response.getBytes(Charset.forName("UTF-8"));
            httpExchange.sendResponseHeaders(HTTP_OK, responseBytes.length);
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(responseBytes);
            responseBody.close();
        };
    }

}