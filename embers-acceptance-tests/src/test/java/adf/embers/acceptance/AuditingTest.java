package adf.embers.acceptance;

import adf.embers.tools.EmbersServer;
import adf.embers.tools.YatspecHttpCaller;
import adf.embers.tools.YatspecQueryInserter;
import com.googlecode.yatspec.junit.Notes;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.state.givenwhenthen.GivensBuilder;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static adf.embers.tools.QueryInserter.ALL_QUERIES;

@RunWith(SpecRunner.class)
@Notes("Performance of the Embers Query Servlet is audited to a database table.")
public class AuditingTest extends TestState {

    @ClassRule
    public static EmbersServer embersServer = new EmbersServer();

    private YatspecQueryInserter yatspecQueryInserter = new YatspecQueryInserter(this, embersServer.getEmbersDatabase().getDataSource());
    private YatspecHttpCaller http = new YatspecHttpCaller(this, embersServer.getContextPath());

    @Test
    public void auditAnExistingQueryThatRespondsWithData() throws Exception {
        given(allQueries());
        when(http.getRequestFor(ALL_QUERIES));
        //TODO show audit
    }

    private GivensBuilder allQueries() {
        return givens -> {
            yatspecQueryInserter.insertAllQueries();
            return givens;
        };
    }
}
