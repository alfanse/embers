package adf.embers.tools;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.IOException;
import java.net.HttpURLConnection;

import static adf.embers.decode.UrlTools.encodeString;

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
    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection) throws IOException {
        //none required for get
    }
}
