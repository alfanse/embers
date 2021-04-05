package adf.embers.query;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
