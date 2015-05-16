package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.CapturedInputAndOutputs;
import com.googlecode.yatspec.state.givenwhenthen.TestLogger;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;

public class YatspecHttpGetCommand extends YatspecHttpCommand {

    public YatspecHttpGetCommand(TestLogger testLogger) {
        super(testLogger);
    }

    @Override
    protected void addRequestDetails(CapturedInputAndOutputs capturedInputAndOutputs, HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException {
        //none required for get
    }
}
