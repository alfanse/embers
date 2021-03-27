package yatspec.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseWrapper {
    private final int responseCode;
    private List<Exception> exceptions = new ArrayList<>();
    private Map<String, Object> responseHeaders  = new HashMap<>();
    private String responseBody;

    public HttpResponseWrapper(int responseCode) {
        this.responseCode = responseCode;
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

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public void setResponseHeaders(Map<String, Object> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
