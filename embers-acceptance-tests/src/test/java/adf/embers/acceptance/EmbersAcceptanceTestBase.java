package adf.embers.acceptance;

import adf.embers.tools.EmbersServer;
import com.googlecode.yatspec.junit.SpecResultListener;
import com.googlecode.yatspec.junit.SpecRunner;
import com.googlecode.yatspec.junit.WithCustomResultListeners;
import com.googlecode.yatspec.rendering.html.HtmlResultRenderer;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import yatspec.renderers.HttpConnectionRenderer;
import yatspec.renderers.HttpUrlConnectionWrapper;
import yatspec.renderers.ResultSetRenderer;
import yatspec.renderers.ResultSetWrapper;

import java.util.ArrayList;

@RunWith(SpecRunner.class)
public abstract class EmbersAcceptanceTestBase extends TestState implements WithCustomResultListeners{

    public static EmbersServer embersServer;

    @BeforeClass
    public static void startServerOnlyOnce() throws Throwable {
        if(embersServer == null ){
            startServer();
        }
    }

    @Before
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
    public Iterable<SpecResultListener> getResultListeners() throws Exception {
        return new ArrayList<SpecResultListener>() {{
            add(new HtmlResultRenderer()
                    .withCustomRenderer(ResultSetWrapper.class, new ResultSetRenderer())
                    .withCustomRenderer(HttpUrlConnectionWrapper.class, new HttpConnectionRenderer()));
        } };
    }

}
