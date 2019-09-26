package adf.embers.examples.spring;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.query.QueryHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {SpringDataSourceConfiguration.class, Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=8080"})
public class ApplicationTest {

    @Test
    public void queryGet_whenNotExists_response404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(embersUri("http://localhost:8080", QueryHandler.PATH))
                .GET()
                .build();
        whenRequest(request, response -> {
            assertThat(response.statusCode()).isEqualTo(404);
            assertThat(response.body()).isEqualTo("Query not found: unknownQuery");
        });
    }

    private URI embersUri(String s, String path) {
        return URI.create(s + path + "/unknownQuery");
    }

    private void whenRequest(HttpRequest request, Consumer<HttpResponse<String>> assertThatFn) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThatFn.accept(response);
    }

    @Test
    public void adminDelete_WhenNotExists_respondsOk() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(embersUri("http://localhost:8080", AdminQueryHandler.PATH))
                .DELETE()
                .build();
        whenRequest(request, response -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("Successfully deleted query");
        });
    }

    @Test
    public void cachedGetQuery_WhenNotExists_responds404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(embersUri("http://localhost:8080", QueryResultCacheHandler.PATH))
                .GET()
                .build();

        whenRequest(request, response -> {
            assertThat(response.statusCode()).isEqualTo(404);
            assertThat(response.body()).isEqualTo("Query not found: unknownQuery");
        });
    }


}