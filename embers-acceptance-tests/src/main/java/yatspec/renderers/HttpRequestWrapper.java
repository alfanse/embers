package yatspec.renderers;

import java.util.List;
import java.util.Map;

public class HttpRequestWrapper {
    private final String requestUrl;
    private final String requestMethod;
    private Map<String, List<String>> requestProperties;
    private String requestBody;

    public HttpRequestWrapper(String requestUrl, String requestMethod) {
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
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
}
