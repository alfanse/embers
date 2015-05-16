package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.TestState;
import yatspec.http.RequestBodyProducer;
import yatspec.http.YatspecHttpPostCommand;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class YatspecHttpPostCommandBuilder {
    public static final String PARAM_QUERY_NAME = "name";
    public static final String PARAM_SQL = "sql";
    public static final String PARAM_DESCRIPTION = "description";

    private final TestState testLogger;

    private String url;
    private String queryName;
    private String sql;
    private String description;

    public YatspecHttpPostCommandBuilder(TestState testLogger) {
        this.testLogger = testLogger;
    }

    public YatspecHttpPostCommandBuilder withUrl(String url) {
        testLogger.interestingGivens.add("Url", url);
        this.url = url;
        return this;
    }

    public YatspecHttpPostCommandBuilder withQueryName(String queryName) {
        testLogger.interestingGivens.add(PARAM_QUERY_NAME, queryName);
        this.queryName = queryName;
        return this;
    }

    public YatspecHttpPostCommandBuilder withQuerySql(String sql) {
        testLogger.interestingGivens.add(PARAM_SQL, sql);
        this.sql = sql;
        return this;
    }

    public YatspecHttpPostCommandBuilder withQueryDescription(String description) {
        testLogger.interestingGivens.add(PARAM_DESCRIPTION, description);
        this.description = description;
        return this;
    }

    public YatspecHttpPostCommand build() {
        return new YatspecHttpPostCommand(testLogger, url, getRequestBodyProducer());
    }

    private RequestBodyProducer getRequestBodyProducer() {
        return () -> {
                    String charset = StandardCharsets.UTF_8.name();
                    try {
                        return String.format("{\"%s\":\"%s\", \"%s\":\"%s\", \"%s\":\"%s\"}",
                                PARAM_QUERY_NAME,
                                URLEncoder.encode(queryName, charset),
                                PARAM_SQL,
                                URLEncoder.encode(sql, charset),
                                PARAM_DESCRIPTION,
                                URLEncoder.encode(description, charset)
                        );
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                };
    }

}
