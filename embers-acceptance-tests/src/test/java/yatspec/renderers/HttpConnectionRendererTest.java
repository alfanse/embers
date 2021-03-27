package yatspec.renderers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpConnectionRendererTest {

    private final HttpUrlConnectionWrapper httpUrlConnectionWrapper = mock(HttpUrlConnectionWrapper.class);

    @Test
    public void rendersExceptionsWithRequestAndResponse() throws Exception {
        String message1 = "Bad thing happened";
        String message2 = "another error message";
        when(httpUrlConnectionWrapper.getExceptions()).thenReturn(asList(
                new IOException(message1),
                new RuntimeException(message2)
        ));

        String render = new HttpConnectionRenderer().render(httpUrlConnectionWrapper);

        assertThat(render).containsOnlyOnce(message1).containsOnlyOnce(message2);
    }

    @Test
    public void renderRequest() throws Exception {
        String requestMethod = "a request method a";
        String requestUrl = "a request url a";
        String requestBody = "a request body a";
        when(httpUrlConnectionWrapper.getRequestMethod()).thenReturn(requestMethod);
        when(httpUrlConnectionWrapper.getRequestBody()).thenReturn(requestBody);
        when(httpUrlConnectionWrapper.getRequestUrl()).thenReturn(requestUrl);
        when(httpUrlConnectionWrapper.getRequestProperties()).thenReturn(
           new HashMap<String, List<String>>(){{
               put("reqProp1", asList("reqProp1Value1","reqProp1Value2"));
           }});

        String render = new HttpConnectionRenderer().render(httpUrlConnectionWrapper);
        assertThat(render)
                .containsOnlyOnce("Request URL: "+requestUrl)
                .containsOnlyOnce("Request Method: "+requestMethod)
                .containsOnlyOnce("Request Properties: "+"{reqProp1=[reqProp1Value1, reqProp1Value2]}")
                .containsOnlyOnce("Request Body: "+requestBody);
    }

    @Test
    public void renderResponse() throws Exception {
        String responseBody = "the response body";
        when(httpUrlConnectionWrapper.getResponseCode()).thenReturn(200);
        when(httpUrlConnectionWrapper.getResponseBody()).thenReturn(responseBody);
        when(httpUrlConnectionWrapper.getResponseHeaders()).thenReturn(
           new HashMap<String, Object>(){{
               put("header1", "header1Value");
           }});

        String render = new HttpConnectionRenderer().render(httpUrlConnectionWrapper);
        assertThat(render)
                .containsOnlyOnce("Response Code: "+200)
                .containsOnlyOnce("Response Body: "+responseBody)
                .containsOnlyOnce("Response Headers: {header1=header1Value}");
    }


}