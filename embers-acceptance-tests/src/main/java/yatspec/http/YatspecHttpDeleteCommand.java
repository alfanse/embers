package yatspec.http;

import com.googlecode.yatspec.state.givenwhenthen.TestState;
import yatspec.renderers.HttpUrlConnectionWrapper;

import java.io.IOException;
import java.net.HttpURLConnection;

public class YatspecHttpDeleteCommand extends YatspecHttpCommand {

    public YatspecHttpDeleteCommand(TestState testLogger) {
        super(testLogger);
    }

    @Override
    protected void addRequestDetails(HttpURLConnection connection, HttpUrlConnectionWrapper httpDetails) throws IOException {
        connection.setRequestMethod("DELETE");
    }
}
