package yatspec.renderers;

import com.googlecode.yatspec.rendering.Renderer;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;

/**
 * Yatspec renderer for HttpUrlConnectionWrapper
 *     to render Http request essentials.
 */
public class HttpConnectionRenderer implements Renderer<HttpUrlConnectionWrapper> {

    @Override
    public String render(HttpUrlConnectionWrapper httpUrlConnectionWrapper) {

        String exceptions = "";
        if(!httpUrlConnectionWrapper.getExceptions().isEmpty()) {
            exceptions = "\nExceptions: " + httpUrlConnectionWrapper.getExceptions();
        }

        String request = String.format("%s %s\nHeaders: %s\nBody: %s",
                httpUrlConnectionWrapper.getRequestMethod(),
                httpUrlConnectionWrapper.getRequestUrl(),
                httpUrlConnectionWrapper.getRequestProperties(),
                httpUrlConnectionWrapper.getRequestBody()
        );

        String response = String.format("Response Code: %s\nHeaders: %s\nBody: %s",
                httpUrlConnectionWrapper.getResponseCode(),
                httpUrlConnectionWrapper.getResponseHeaders(),
                httpUrlConnectionWrapper.getResponseBody());

        return request + "\n\n" + response + exceptions;
    }

    public String htmlRender(HttpUrlConnectionWrapper httpUrlConnectionWrapper) {
        String exceptions = "";
        if(!httpUrlConnectionWrapper.getExceptions().isEmpty()) {
            exceptions = "<br/>" +
                    "<div>" +
                        "<span>Exceptions: " + httpUrlConnectionWrapper.getExceptions() + "</span>" +
                    "</div>";
        }

        return new SafeString("<div>" +
                    "<div>" +
                        "<span>Request URL: "+httpUrlConnectionWrapper.getRequestUrl()+"</span><br/>" +
                        "<span>Request Method: "+httpUrlConnectionWrapper.getRequestMethod()+"</span><br/>" +
                        "<span>Request Properties: "+httpUrlConnectionWrapper.getRequestProperties()+"</span><br/>" +
                        "<span>Request Body: "+httpUrlConnectionWrapper.getRequestBody()+"</span>" +
                    "</div>" +
                    "<br/>" +
                    "<div>" +
                        "<span>Response Code: "+httpUrlConnectionWrapper.getResponseCode()+"</span><br/>" +
                        "<span>Response Headers: "+httpUrlConnectionWrapper.getResponseHeaders()+"</span><br/>" +
                        "<span>Response Body: "+httpUrlConnectionWrapper.getResponseBody()+"</span>" +
                    "</div>" +
                    exceptions +
               "</div>").toString();
    }

}
