package adf.embers.tools;

import adf.embers.query.persistence.Query;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import javax.sql.DataSource;

public class YatspecQueryInserter {

    QueryInserter queryInserter;

    public YatspecQueryInserter(final TestState classUnderTest, final DataSource dataSource) {
        this.queryInserter = new QueryInserter(dataSource, query -> {
            classUnderTest.interestingGivens().add("Expected Query Name To Run", query.getName());
            classUnderTest.interestingGivens().add("Expected Query Sql To Run", query.getSql());
        });
    }

    public void allQueries() {
        queryInserter.insertAllQueries();
    }

    public void insertQuery(final Query query) {
        queryInserter.insertQuery(query);
    }
}
