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
import yatspec.renderers.HttpRequestRenderer;
import yatspec.renderers.HttpRequestWrapper;
import yatspec.renderers.HttpResponseRenderer;
import yatspec.renderers.HttpResponseWrapper;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.googlecode.yatspec.sequence.Participants.ACTOR;
import static com.googlecode.yatspec.sequence.Participants.PARTICIPANT;

@ExtendWith(SequenceDiagramExtension.class)
public abstract class EmbersAcceptanceTestBase implements WithParticipants, WithCustomResultListeners {

    public static EmbersServer embersServer;

    public TestState interactions = new TestState();

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
                    .withCustomRenderer(HttpRequestWrapper.class, (result) -> new HttpRequestRenderer())
                    .withCustomRenderer(HttpResponseWrapper.class, (result) -> new HttpResponseRenderer()));
        } };
    }

    @Override
    public List<Participant> participants() {
        return new ArrayList<Participant>(){{
            add(ACTOR.create("Client"));
            add(PARTICIPANT.create("Embers"));
        }};
    }

    public <T> void then(T actual, Matcher<? super T> matcher) {
        MatcherAssert.assertThat(actual, matcher);
    }
}
