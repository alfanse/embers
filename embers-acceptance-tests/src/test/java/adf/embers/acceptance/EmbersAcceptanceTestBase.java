package adf.embers.acceptance;

import adf.embers.tools.EmbersServer;
import com.googlecode.yatspec.junit.SequenceDiagramExtension;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.junit.WithParticipants;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.sequence.Participant;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import yatspec.renderers.HttpConnectionRenderer;
import yatspec.renderers.HttpUrlConnectionWrapper;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ExtendWith(SequenceDiagramExtension.class)
public abstract class EmbersAcceptanceTestBase implements WithParticipants, WithCustomResultListeners {

    public static EmbersServer embersServer;

    public TestState interactions = new TestState();
    private List<Participant> participants = new ArrayList<>();

    @BeforeAll
    public static void startServerOnlyOnce() throws Throwable {
        if(embersServer == null ){
            startServer();
        }
    }

    @BeforeEach
    public void resetDatabase(){
        embersServer.getEmbersDatabase().clearEmbersTables();
    }

    private static synchronized void startServer() throws Throwable {
        if(embersServer==null){
            embersServer = new EmbersServer();
            embersServer.before();
        }
    }

    @Override
    public Collection<SpecResultListener> getResultListeners() {
        return new ArrayList<SpecResultListener>() {{
            add(new HtmlResultRenderer()
                    .withCustomRenderer(ResultSetWrapper.class, (result) -> new ResultSetRenderer())
                    .withCustomRenderer(HttpUrlConnectionWrapper.class, (result) -> new HttpConnectionRenderer()));
        } };
    }

    @Override
    public List<Participant> participants() {
        return participants;
    }

    public void then(Object actual, Matcher matcher) {
        MatcherAssert.assertThat(actual, matcher);
    }
}
