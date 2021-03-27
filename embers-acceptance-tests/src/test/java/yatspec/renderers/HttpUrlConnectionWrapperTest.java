package yatspec.renderers;

import org.fest.assertions.data.MapEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUrlConnectionWrapperTest {


    private final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
    private final HttpUrlConnectionWrapper wrapper = new HttpUrlConnectionWrapper();

    @Test
    public void extractsResponseCode() throws IOException {
        when(httpURLConnection.getResponseCode()).thenReturn(200);
        when(httpURLConnection.getInputStream()).thenReturn(mock(InputStream.class));

        whenTheConnectionResponseIsScraped();

        assertThat(wrapper.getResponseCode()).isEqualTo(200);
    }

    @Test
    public void preserveExceptionWhenResponseCodeErrors() throws IOException {
        IOException thrown = new IOException("bad things happened");
        when(httpURLConnection.getResponseCode()).thenThrow(thrown);

        when(httpURLConnection.getErrorStream()).thenReturn(mock(InputStream.class));

        whenTheConnectionResponseIsScraped();

        assertThat(wrapper.getResponseCode()).isEqualTo(0);
        assertThat(wrapper.getExceptions()).contains(thrown);
    }

    @Test
    public void extractContentType() throws IOException {
        String value = "content type";
        when(httpURLConnection.getContentType()).thenReturn(value);
        when(httpURLConnection.getResponseCode()).thenReturn(200);
        when(httpURLConnection.getInputStream()).thenReturn(mock(InputStream.class));

        whenTheConnectionResponseIsScraped();

        assertThat(wrapper.getResponseHeaders()).contains(MapEntry.entry("Content-Type", value));
    }

    private void whenTheConnectionResponseIsScraped() {
        wrapper.captureResponseDetails(httpURLConnection);
    }
}