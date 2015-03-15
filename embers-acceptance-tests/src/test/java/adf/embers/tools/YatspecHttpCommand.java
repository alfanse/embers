package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class YatspecHttpCommand {
    protected final TestLogger testLogger;
    private String url;

    public YatspecHttpCommand(TestLogger testLogger) {
        this.testLogger = testLogger;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public ActionUnderTest execute() {
        return (givens, capturedInputAndOutputs) -> {
            HttpURLConnection conn = openConnection(url, givens);
            try {
                addRequestDetails(capturedInputAndOutputs, conn);
                captureResponse(conn);
            } finally {
                conn.disconnect();
            }

            return capturedInputAndOutputs;
        };
    }

    protected abstract void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection) throws IOException;

    protected HttpURLConnection openConnection(String url, InterestingGivens givens) throws IOException {
        URL location = new URL(url);
        givens.add("Url", location.toExternalForm());
        return (HttpURLConnection) location.openConnection();
    }

    protected void captureResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        testLogger.log("Response Code", responseCode);
        testLogger.log("Response Content-Type", conn.getContentType());
        testLogger.log("Response Content Length", conn.getContentLengthLong());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = conn.getInputStream()) {
                captureInputStream(inputStream, "Response Body");
            }
        } else {
            try (InputStream inputStream = conn.getErrorStream()) {
                captureInputStream(inputStream, "Error Response Body");
            }
        }
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
