package adf.embers.query;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static adf.embers.statics.UrlTools.decodeString;

@Path(QueryHandler.PATH)
public class QueryHandler {

    public static final String PATH = "/query";

    private final QueryProcessor queryProcessor;

    @Inject
    public QueryHandler(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @GET
    @Path("{queryName}")
    public Response executeQuery(@PathParam("queryName") String queryName) {
        QueryResult queryResult = queryProcessor.placeQuery(() -> decodeString(queryName));

        if (queryResult.hasErrors()) {
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(queryResult.getErrorMessages())
                    .build();
        }

        return Response.ok(queryResult.getResult()).type("text/csv").build();
    }
}
