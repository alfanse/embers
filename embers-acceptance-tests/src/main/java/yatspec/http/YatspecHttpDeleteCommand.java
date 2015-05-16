package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;

public class YatspecHttpDeleteCommand extends YatspecHttpCommand {

    public YatspecHttpDeleteCommand(TestLogger testLogger) {
        super(testLogger);
    }

    @Override
    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException {
        connection.setRequestMethod("DELETE");
    }
}
