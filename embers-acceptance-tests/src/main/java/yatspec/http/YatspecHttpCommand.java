package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.TestState;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public abstract class YatspecHttpCommand {
    //honoring the yatspec sequence diagram template: X from Y to Z
    public static final String LOG_KEY_SUFFIX_FOR_HTTP_REQUEST = "request from Client to Embers";
    public static final String LOG_KEY_SUFFIX_FOR_HTTP_RESPONSE = "response from Embers to Client";

    protected final TestState yatspec;
    private final HttpUrlConnectionWrapper httpDetails = new HttpUrlConnectionWrapper();
    private String logPrefix = "";

    public YatspecHttpCommand(TestState yatspec) {
        this.yatspec = yatspec;
    }

    public void setUrl(String url) {
        httpDetails.setRequestUrl(url);
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public void execute() throws Exception{
        HttpURLConnection conn = openConnection();
        try {
            addRequestDetails(conn, httpDetails);
            httpDetails.captureRequestDetails(conn);
            httpDetails.captureResponseDetails(conn);
        } finally {
            yatspec.log(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP_REQUEST), httpDetails.getHttpRequestWrapper());
            yatspec.log(getLogKeyName(LOG_KEY_SUFFIX_FOR_HTTP_RESPONSE), httpDetails.getHttpResponseWrapper());
            conn.disconnect();
        }
    }

    public Integer responseCode() {
        return httpDetails.getResponseCode();
    }

    public String responseBody() {
        return httpDetails.getResponseBody();
    }

    public Map<String, Object> responseHeaders() {
        return httpDetails.getResponseHeaders();
    }

    protected abstract void addRequestDetails(HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException;

    private HttpURLConnection openConnection() throws IOException {
        URL location = new URL(httpDetails.getRequestUrl());
        yatspec.interestingGivens().add("Url", location.toExternalForm());
        return (HttpURLConnection) location.openConnection();
    }

    private String getLogKeyName(String logKeySuffix) {
        return logPrefix + " " + logKeySuffix;
    }
}
