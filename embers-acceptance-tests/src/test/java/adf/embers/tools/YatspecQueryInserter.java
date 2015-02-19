package adf.embers.tools;

import adf.embers.query.persistence.Query;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

import javax.sql.DataSource;

public class YatspecQueryInserter extends QueryInserter {
    private TestState classUnderTest;

    public YatspecQueryInserter(TestState classUnderTest, DataSource dataSource) {
        super(dataSource);
        this.classUnderTest = classUnderTest;
    }

    @Override
    public void insertQuery(Query query) {
        classUnderTest.interestingGivens.add("Expected Query Name To Run", query.getName());
        classUnderTest.interestingGivens.add("Expected Query Sql To Run", query.getSql());
        super.insertQuery(query);
    }
}
