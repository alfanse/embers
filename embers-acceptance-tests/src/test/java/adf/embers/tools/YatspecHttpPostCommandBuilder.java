package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.TestState;

public class YatspecHttpPostCommandBuilder {
    public static final String PARAM_QUERY_NAME = "queryName";
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

    public YatspecHttpPostCommandBuilder withName(String queryName) {
        testLogger.interestingGivens.add(PARAM_QUERY_NAME, queryName);
        this.queryName = queryName;
        return this;
    }

    public YatspecHttpPostCommandBuilder withSql(String sql) {
        testLogger.interestingGivens.add(PARAM_SQL, sql);
        this.sql = sql;
        return this;
    }

    public YatspecHttpPostCommandBuilder withDescription(String description) {
        testLogger.interestingGivens.add(PARAM_DESCRIPTION, description);
        this.description = description;
        return this;
    }

    public YatspecHttpPostCommand build() {
        return new YatspecHttpPostCommand(testLogger, url, queryName, sql, description);
    }

}
