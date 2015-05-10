package yatspec.renderers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUrlConnectionWrapper {
    private String requestUrl;
    private String requestMethod;

    private int responseCode;
    private List<Exception> exceptions = new ArrayList<>();
    private Map<String, Object> responseHeaders  = new HashMap<>();
    private String requestBody;
    private Map<String, List<String>> requestProperties;
    private String responseBody;

    public void captureRequestDetails(HttpURLConnection connection) {
        requestMethod = connection.getRequestMethod();
    }

    public void setRequestUrl(String url) {
        this.requestUrl = url;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestProperties(Map<String, List<String>> requestProperties) {
        this.requestProperties = requestProperties;
    }

    public Map<String, List<String>> getRequestProperties() {
        return requestProperties;
    }

    public void captureResponseDetails(HttpURLConnection connection) {
        safelyCaptureResponseCode(connection);
        responseHeaders.put("Content-Type", connection.getContentType());
        responseHeaders.putAll(connection.getHeaderFields());

        try (InputStream inputStream = (HttpURLConnection.HTTP_OK == responseCode ?
                connection.getInputStream() : connection.getErrorStream())) {
            responseBody = readInputStream(inputStream);
        } catch (IOException e) {
            exceptions.add(e);
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public Map<String, Object> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private void safelyCaptureResponseCode(HttpURLConnection connection) {
        try {
            responseCode = connection.getResponseCode();
        } catch (Exception e) {
            exceptions.add(e);
        }
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
