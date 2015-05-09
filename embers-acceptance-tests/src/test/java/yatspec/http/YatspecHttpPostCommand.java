package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class YatspecHttpPostCommand extends YatspecHttpCommand {
    private final String queryName;
    private final String sql;
    private final String description;

    public YatspecHttpPostCommand(TestLogger testLogger, String url, String queryName, String sql, String description) {
        super(testLogger);
        super.setUrl(url);
        this.queryName = queryName;
        this.sql = sql;
        this.description = description;
    }

    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection) throws IOException {
        final String charset = StandardCharsets.UTF_8.name();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream output = connection.getOutputStream()) {
            final String query = getQueryParametersWithEncodedValues(charset);
            capturedInputAndOutputs.add("Request Body", query);
            output.write(query.getBytes(charset));
        }
    }

    private String getQueryParametersWithEncodedValues(String charset) throws UnsupportedEncodingException {
        return String.format("{\"%s\":\"%s\", \"%s\":\"%s\", \"%s\":\"%s\"}",
                YatspecHttpPostCommandBuilder.PARAM_QUERY_NAME,
                URLEncoder.encode(queryName, charset),
                YatspecHttpPostCommandBuilder.PARAM_SQL,
                URLEncoder.encode(sql, charset),
                YatspecHttpPostCommandBuilder.PARAM_DESCRIPTION,
                URLEncoder.encode(description, charset)
        );
    }
}
