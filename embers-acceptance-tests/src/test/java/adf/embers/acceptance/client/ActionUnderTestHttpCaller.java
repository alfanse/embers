package adf.embers.acceptance.client;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActionUnderTestHttpCaller {

    private final TestLogger testLogger;

    public ActionUnderTestHttpCaller(TestLogger testLogger) {
        this.testLogger = testLogger;
    }

    public ActionUnderTest getRequestFor(String url) {
        return (givens, capturedInputAndOutputs) -> {
            URL u = new URL(url);
            givens.add("Url", u.toExternalForm());

            HttpURLConnection conn = (HttpURLConnection)u.openConnection();
            try {
                int responseCode = conn.getResponseCode();
                testLogger.log("Response Code", responseCode);
                testLogger.log("Response Content-Type", conn.getContentType());
                testLogger.log("Response Content Length", conn.getContentLengthLong());

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = conn.getInputStream()) {
                        captureInputStream(inputStream, "Response Body");
                    }
                } else {
                    try (InputStream inputStream = conn.getErrorStream()) {
                        captureInputStream(inputStream, "Error Response Body");
                    }
                }
            } finally {
                conn.disconnect();
            }

            return capturedInputAndOutputs;
        };
    }

    private void captureInputStream(InputStream inputStream, String logKey) throws IOException {
        testLogger.log(logKey, readInputStream(inputStream));
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader((new InputStreamReader(inputStream)))) {
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
