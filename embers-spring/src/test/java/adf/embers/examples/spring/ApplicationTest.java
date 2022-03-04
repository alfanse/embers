package adf.embers.examples.spring;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.query.QueryHandler;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {SpringDataSourceConfiguration.class, Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=8080"})
public class ApplicationTest {

    @Test
    public void query_accessible() throws IOException, InterruptedException {
        String responseBody =
                RestAssured.when()
                        .get(embersUri(QueryHandler.PATH + "/unknownQuery")).
                        then().statusCode(404)
                        .extract().response().body().asString();

        assertThat(responseBody).isEqualTo("Query not found: unknownQuery");
    }

    @Test
    public void admin_accessible() throws IOException, InterruptedException {
        String responseBody = RestAssured.when()
                .delete(embersUri(AdminQueryHandler.PATH + "/unknownQuery"))
                .then().statusCode(200)
                .extract().response().body().asString();

        assertThat(responseBody).isEqualTo("Successfully deleted query");
    }

    @Test
    public void cached_accessible() throws IOException, InterruptedException {
        String responseBody = RestAssured.when()
                .get(embersUri(QueryResultCacheHandler.PATH + "/unknownQuery"))
                .then().statusCode(404)
                .extract().response().body().asString();

            assertThat(responseBody).isEqualTo("Query not found: unknownQuery");
    }

    private URI embersUri(String endpoint) {
        return URI.create("http://localhost:8080/embers" + endpoint);
    }

}
