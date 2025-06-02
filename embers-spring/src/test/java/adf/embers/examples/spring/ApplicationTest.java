package adf.embers.examples.spring;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.query.QueryHandler;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = {Application.class, SpringDataSourceConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApplicationTest {
    private static final Logger log = Logger.getLogger(ApplicationTest.class.getName());

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        log.info("Using port: " + port);
    }

    @Test
    public void query_accessible() {
        String responseBody =
            RestAssured.when()
                .get(embersUri(QueryHandler.PATH + "/unknownQuery"))
                .then()
                .statusCode(404)
                .extract().response().body().asString();

        assertThat(responseBody).isEqualTo("Query not found: unknownQuery");
    }

    @Test
    public void admin_accessible() {
        String responseBody = RestAssured.when()
                .delete(embersUri(AdminQueryHandler.PATH + "/unknownQuery"))
                .then().statusCode(200)
                .extract().response().body().asString();

        assertThat(responseBody).isEqualTo("Successfully deleted query");
    }

    @Test
    public void cached_accessible() {
        String responseBody = RestAssured.when()
                .get(embersUri(QueryResultCacheHandler.PATH + "/unknownQuery"))
                .then().statusCode(404)
                .extract().response().body().asString();

            assertThat(responseBody).isEqualTo("Query not found: unknownQuery");
    }

    private URI embersUri(String path) {
        String uri = String.format("http://localhost:%d/embers%s", 
            port, path.startsWith("/") ? path : "/" + path);
        log.info("Calling URI: " + uri);
        return URI.create(uri);
    }

}
