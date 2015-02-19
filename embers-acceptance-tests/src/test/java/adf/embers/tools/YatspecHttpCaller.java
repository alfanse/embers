package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.StateExtractor;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YatspecHttpCaller {

    private final TestLogger testLogger;
    private final String contextPath;

    public YatspecHttpCaller(TestLogger testLogger, String contextPath) {
        this.testLogger = testLogger;
        this.contextPath = contextPath;
    }

    public ActionUnderTest getRequestFor(String queryName) {
        return loggedGetRequest(contextPath + "/" + queryName);
    }

    public StateExtractor<Integer> responseCode() {
        return inputAndOutput -> inputAndOutput.getType("Response Code", Integer.class);
    }

    public StateExtractor<String> theErrorResponse() {
        return inputAndOutput -> inputAndOutput.getType("Error Response Body", String.class);
    }

    public StateExtractor<String> responseBody() {
        return inputAndOutput -> inputAndOutput.getType("Response Body", String.class);
    }

    private ActionUnderTest loggedGetRequest(String url) {
        return (givens, capturedInputAndOutputs) -> {
            URL location = new URL(url);
            givens.add("Url", location.toExternalForm());

            HttpURLConnection conn = (HttpURLConnection)location.openConnection();
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

    private String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader((new InputStreamReader(inputStream)))) {
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private void captureInputStream(InputStream inputStream, String logKey) throws IOException {
        testLogger.log(logKey, readInputStream(inputStream));
    }
}
