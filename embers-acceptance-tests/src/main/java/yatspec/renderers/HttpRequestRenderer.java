package yatspec.renderers;

import com.googlecode.yatspec.rendering.Renderer;

/**
 * Yatspec renderer for HttpRequestWrapper
 *     to render Http request essentials.
 */
public class HttpRequestRenderer implements Renderer<HttpRequestWrapper> {

    @Override
    public String render(HttpRequestWrapper requestWrapper) {
        return String.format("%s %s\nHeaders: %s\nBody: %s",
                requestWrapper.getRequestMethod(),
                requestWrapper.getRequestUrl(),
                requestWrapper.getRequestProperties(),
                requestWrapper.getRequestBody()
        );
    }
}
