package adf.embers.tools;

import adf.embers.query.persistence.Query;
import com.googlecode.yatspec.state.givenwhenthen.GivensBuilder;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import javax.sql.DataSource;

public class YatspecQueryInserter {

    QueryInserter queryInserter;

    public YatspecQueryInserter(final TestState classUnderTest, final DataSource dataSource) {
        this.queryInserter = new QueryInserter(dataSource, query -> {
            classUnderTest.interestingGivens.add("Expected Query Name To Run", query.getName());
            classUnderTest.interestingGivens.add("Expected Query Sql To Run", query.getSql());
        });
    }

    public GivensBuilder allQueries() {
        return givens -> {
            queryInserter.insertAllQueries();
            return givens;
        };
    }

    public GivensBuilder insertQuery(final Query query) {
        return givens -> {
            queryInserter.insertQuery(query);
            return givens;
        };
    }
}
