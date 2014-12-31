package adf.embers.acceptance;

import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpecRunner.class)
public class QueryApiTest extends TestState {

    public static final String CONTEXT_PATH = "embers";
    public static final String PATH_TO_QUERY = "/" + CONTEXT_PATH + "/query";
    public static final int PORT = 9000;
    private HttpServer server;

    @Before
    public void startHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext(PATH_TO_QUERY, handlerForQuery());
        Executor useDefaultExecutor = null;
        server.setExecutor(useDefaultExecutor);
        server.start();
    }

    @After
    public void stopHttpServer(){
        server.stop(1);
    }

    @Test
    public void querySingleTable() throws Exception {
        when(userGetsQuery());

        then(theResponse(), isValid());
    }

    private ActionUnderTest userGetsQuery() throws IOException {
        return (givens, capturedInputAndOutputs) -> {
            URL u = new URL("http://localhost:9000"+PATH_TO_QUERY);
            givens.add("Url", u.toExternalForm());

            URLConnection conn = u.openConnection();
            InputStream inputStream = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            try( BufferedReader br = new BufferedReader((new InputStreamReader(inputStream)))) {
                for ( String line; (line = br.readLine()) != null; ) {
                    sb.append(line);
                }
            }
            log("Response Body", sb);
            log("Response Content-Type", conn.getContentType());
            log("Response Content Length", conn.getContentLengthLong());
            log("Response Code", ((HttpURLConnection )conn).getResponseCode());

            //todo httpUrlConnection renderer
            log("HttpUrlConnection", (HttpURLConnection )conn);
            ((HttpURLConnection) conn).disconnect();
            return capturedInputAndOutputs;
        };
    }

    private CustomTypeSafeMatcher<HttpURLConnection> isValid() {
        return new CustomTypeSafeMatcher<HttpURLConnection>("description") {
            @Override
            protected boolean matchesSafely(HttpURLConnection item) {
                assertThat(item.getContentType()).isEqualTo("text/plain");
                try {
                    assertThat(item.getResponseCode()).isEqualTo(200);
                } catch (IOException e) {
                    throw new RuntimeException("Unexpected response code", e);
                }

                return true;
            }
        };
    }

    private StateExtractor<HttpURLConnection> theResponse() {
        return inputAndOutputs -> inputAndOutputs.getType("HttpUrlConnection", HttpURLConnection.class);
    }

    private HttpHandler handlerForQuery() {
        return httpExchange -> {
            //todo use production code to build Response and Content-Type
            String response = "hello world";
            String contentType = "text/plain";
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type", contentType);
            byte[] responseBytes = response.getBytes(Charset.forName("UTF-8"));
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(responseBytes);
            responseBody.close();
        };
    }

}