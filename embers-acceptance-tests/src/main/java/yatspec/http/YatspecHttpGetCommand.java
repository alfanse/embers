package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;

import static adf.embers.statics.UrlTools.encodeString;

public class YatspecHttpGetCommand extends YatspecHttpCommand {

    private final String contextPath;

    public YatspecHttpGetCommand(TestLogger testLogger, String contextPath) {
        super(testLogger);
        this.contextPath = contextPath;
    }

    public ActionUnderTest getRequestFor(String queryName) {
        setUrl(contextPath + "/" + encodeString(queryName));
        return execute();
    }

    @Override
    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException {
        //none required for get
    }
}
