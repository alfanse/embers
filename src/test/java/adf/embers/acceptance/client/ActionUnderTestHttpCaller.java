package adf.embers.acceptance.client;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ActionUnderTestHttpCaller {

    private final TestLogger testLogger;

    public ActionUnderTestHttpCaller(TestLogger testLogger) {
        this.testLogger = testLogger;
    }

    public ActionUnderTest getRequestFor(String url) throws IOException {
        return (givens, capturedInputAndOutputs) -> {
            URL u = new URL(url);
            givens.add("Url", u.toExternalForm());

            URLConnection conn = u.openConnection();
            InputStream inputStream = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader((new InputStreamReader(inputStream)))) {
                for (String line; (line = br.readLine()) != null; ) {
                    sb.append(line);
                }
            }

            testLogger.log("Response Body", sb.toString());
            testLogger.log("Response Content-Type", conn.getContentType());
            testLogger.log("Response Content Length", conn.getContentLengthLong());

            HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
            testLogger.log("Response Code", httpURLConnection.getResponseCode());
            testLogger.log("HttpUrlConnection", httpURLConnection);//todo build renderer

            httpURLConnection.disconnect();
            return capturedInputAndOutputs;
        };
    }
}
