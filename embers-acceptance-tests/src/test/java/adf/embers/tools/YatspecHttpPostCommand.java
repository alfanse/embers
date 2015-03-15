package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.InterestingGivens;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class YatspecHttpPostCommand implements ActionUnderTest {
    private final TestLogger testLogger;
    private final String url;
    private final String queryName;
    private final String sql;
    private final String description;

    public YatspecHttpPostCommand(TestLogger testLogger, String url, String queryName, String sql, String description) {
        this.testLogger = testLogger;
        this.url = url;
        this.queryName = queryName;
        this.sql = sql;
        this.description = description;
    }

    @Override
    public CapturedInputAndOutputs execute(InterestingGivens givens, CapturedInputAndOutputs capturedInputAndOutputs) throws Exception {

        final URL destinationUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) destinationUrl.openConnection();

        try {
            final String charset = StandardCharsets.UTF_8.name();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                final String query = getQueryParametersWithEncodedValues(charset);
                capturedInputAndOutputs.add("Request Body", query);
                output.write(query.getBytes(charset));
            }

            int responseCode = connection.getResponseCode();
            testLogger.log("Response Code", responseCode);
            testLogger.log("Response Content-Type", connection.getContentType());
            testLogger.log("Response Content Length", connection.getContentLengthLong());

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream()) {
                    captureInputStream(inputStream, "Response Body");
                }
            } else {
                try (InputStream inputStream = connection.getErrorStream()) {
                    captureInputStream(inputStream, "Error Response Body");
                }
            }
        } finally {
            connection.disconnect();
        }
        return capturedInputAndOutputs;
    }

    private String getQueryParametersWithEncodedValues(String charset) throws UnsupportedEncodingException {
        return String.format("%s=%s&%s=%s&%s=%s",
                YatspecHttpPostCommandBuilder.PARAM_QUERY_NAME,
                URLEncoder.encode(queryName, charset),
                YatspecHttpPostCommandBuilder.PARAM_SQL,
                URLEncoder.encode(sql, charset),
                YatspecHttpPostCommandBuilder.PARAM_DESCRIPTION,
                URLEncoder.encode(description, charset)
        );
    }

    private void captureInputStream(InputStream inputStream, String logKey) throws IOException {
        testLogger.log(logKey, readInputStream(inputStream));
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
