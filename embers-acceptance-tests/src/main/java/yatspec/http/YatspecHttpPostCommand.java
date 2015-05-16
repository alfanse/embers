package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class YatspecHttpPostCommand extends YatspecHttpCommand {
    private RequestBodyProducer requestBodyProducer;

    public YatspecHttpPostCommand(TestLogger testLogger, String url, RequestBodyProducer requestBodyProducer) {
        super(testLogger);
        super.setUrl(url);
        this.requestBodyProducer = requestBodyProducer;
    }

    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException {
        final String charset = StandardCharsets.UTF_8.name();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json");

        httpDetails.setRequestProperties(connection.getRequestProperties());

        try (OutputStream output = connection.getOutputStream()) {
            final String query = requestBodyProducer.produceRequestBody();
            httpDetails.setRequestBody(query);
            output.write(query.getBytes(charset));
        }
    }

}

