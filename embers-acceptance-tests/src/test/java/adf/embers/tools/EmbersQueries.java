package adf.embers.tools;

import adf.embers.query.persistence.Query;
import com.googlecode.yatspec.state.givenwhenthen.TestState;

public class EmbersQueries {


    private TestState classUnderTest;
    private EmbersDatabase embersDatabase;

    public EmbersQueries(TestState classUnderTest, EmbersDatabase embersDatabase) {
        this.classUnderTest = classUnderTest;
        this.embersDatabase = embersDatabase;
    }

    public void insertAllQueries() {
        insertQuery(embersDatabase.allQueries());
    }

    public void insertQuery(Query query) {
        classUnderTest.interestingGivens.add("Expected Query To Run", query.getSql());
        embersDatabase.insertQuery(query);
    }
}
