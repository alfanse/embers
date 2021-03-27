package yatspec.renderers;

import com.googlecode.yatspec.rendering.Renderer;

/**
 * Yatspec renderer for List<Map<String, Object>>
 *     to render as html table with header row and data rows.
 */
public class HttpConnectionRenderer implements Renderer<HttpUrlConnectionWrapper> {

    @Override
    public String render(HttpUrlConnectionWrapper httpUrlConnectionWrapper) {
        String exceptions = "";
        if(!httpUrlConnectionWrapper.getExceptions().isEmpty()) {
            exceptions = "<br/>" +
                    "<div>" +
                        "<span>Exceptions: " + httpUrlConnectionWrapper.getExceptions() + "</span>" +
                    "</div>";
        }

        return "<div>" +
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
               "</div>";
    }

}
