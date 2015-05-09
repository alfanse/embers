package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.ActionUnderTest;
import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;

import java.io.IOException;
import java.net.HttpURLConnection;

import static adf.embers.statics.UrlTools.encodeString;

public class YatspecHttpDeleteCommand extends YatspecHttpCommand {

    private final String contextPath;

    public YatspecHttpDeleteCommand(TestLogger testLogger, String contextPath) {
        super(testLogger);
        this.contextPath = contextPath;
    }

    public ActionUnderTest deleteRequestFor(String queryName) {
        setUrl(contextPath + "/" + encodeString(queryName));
        return execute();
    }

    @Override
    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("DELETE");
    }
}
