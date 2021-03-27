package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.*;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public abstract class YatspecHttpCommand {
    public static final String LOG_KEY_SUFFIX_FOR_HTTP = "Http Details";
    protected final TestState testState;
    private String url;
    private String logPrefix = "";
    private final HttpUrlConnectionWrapper httpDetails = new HttpUrlConnectionWrapper();

    public YatspecHttpCommand(TestState testState) {
        this.testState = testState;
    }

    public void setUrl(String url) {
        this.url = url;
        httpDetails.setRequestUrl(url);
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public void execute() throws Exception{
        HttpURLConnection conn = openConnection(url, testState.interestingGivens());
        try {
            addRequestDetails(conn, httpDetails);
            httpDetails.captureRequestDetails(conn);
            httpDetails.captureResponseDetails(conn);
        } finally {
            testState.log(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP), httpDetails);
            conn.disconnect();
        }
    }

    public Integer responseCode() {
        return fetchCurrentHttpDetailsFromYatspec().getResponseCode();
    }

    public String responseBody() {
        return fetchCurrentHttpDetailsFromYatspec().getResponseBody();
    }

    public Map<String, Object> responseHeaders() {
        return fetchCurrentHttpDetailsFromYatspec().getResponseHeaders();
    }

    protected abstract void addRequestDetails(HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException;

    private HttpURLConnection openConnection(String url, InterestingGivens givens) throws IOException {
        URL location = new URL(url);
        givens.add("Url", location.toExternalForm());
        return (HttpURLConnection) location.openConnection();
    }

    private HttpUrlConnectionWrapper fetchCurrentHttpDetailsFromYatspec() {
//        return inputAndOutput.getType(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP), HttpUrlConnectionWrapper.class);
        return testState.getType(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP), HttpUrlConnectionWrapper.class);
    }

    private String getLogKeyName(String logKeySuffix) {
        return logPrefix + " " + logKeySuffix;
    }
}
