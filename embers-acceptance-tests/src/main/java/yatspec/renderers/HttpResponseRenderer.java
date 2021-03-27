package yatspec.renderers;

import com.googlecode.yatspec.rendering.Renderer;

/**
 * Yatspec renderer for HttpResponseWrapper
 *     to render Http response essentials.
 */
public class HttpResponseRenderer implements Renderer<HttpResponseWrapper> {

    @Override
    public String render(HttpResponseWrapper responseWrapper) {

        String exceptions = "";
        if(!responseWrapper.getExceptions().isEmpty()) {
            exceptions = "\nExceptions: " + responseWrapper.getExceptions();
        }

        String response = String.format("Response Code: %s\nHeaders: %s\nBody: %s",
                responseWrapper.getResponseCode(),
                responseWrapper.getResponseHeaders(),
                responseWrapper.getResponseBody());

        return response + exceptions;
    }
}
