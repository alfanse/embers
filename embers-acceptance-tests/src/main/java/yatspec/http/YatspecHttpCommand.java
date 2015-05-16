package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.*;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class YatspecHttpCommand {
    public static final String LOG_KEY_SUFFIX_FOR_HTTP = "Http Details";
    protected final TestLogger testLogger;
    private String url;
    private String logPrefix = "";
    private final HttpUrlConnectionWrapper httpDetails = new HttpUrlConnectionWrapper();

    public YatspecHttpCommand(TestLogger testLogger) {
        this.testLogger = testLogger;
    }

    public void setUrl(String url) {
        this.url = url;
        httpDetails.setRequestUrl(url);
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public ActionUnderTest execute() {
        return (givens, capturedInputAndOutputs) -> {
            HttpURLConnection conn = openConnection(url, givens);
            try {
                addRequestDetails(capturedInputAndOutputs, conn, httpDetails);
                httpDetails.captureRequestDetails(conn);
                httpDetails.captureResponseDetails(conn);
            } finally {
                testLogger.log(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP), httpDetails);
                conn.disconnect();
            }

            return capturedInputAndOutputs;
        };
    }

    public StateExtractor<Integer> responseCode() {
        return inputAndOutput -> fetchCurrentHttpDetailsFromYatspec(inputAndOutput).getResponseCode();
    }

    public StateExtractor<String> responseBody() {
        return inputAndOutput -> fetchCurrentHttpDetailsFromYatspec(inputAndOutput).getResponseBody();
    }

    protected abstract void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException;

    private HttpURLConnection openConnection(String url, InterestingGivens givens) throws IOException {
        URL location = new URL(url);
        givens.add("Url", location.toExternalForm());
        return (HttpURLConnection) location.openConnection();
    }

    private HttpUrlConnectionWrapper fetchCurrentHttpDetailsFromYatspec(CapturedInputAndOutputs inputAndOutput) {
        return inputAndOutput.getType(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP), HttpUrlConnectionWrapper.class);
    }

    private String getLogKeyName(String logKeySuffix) {
        return logPrefix + " " + logKeySuffix;
    }

}
