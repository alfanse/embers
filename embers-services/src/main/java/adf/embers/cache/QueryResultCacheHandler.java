package adf.embers.cache;

import adf.embers.query.QueryProcessor;
import adf.embers.query.QueryResult;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

import static adf.embers.statics.UrlTools.decodeString;

@Path("/" + QueryResultCacheHandler.PATH)
public class QueryResultCacheHandler {

    public static final String PATH = "cached";

    private final QueryProcessor queryProcessor;

    @Inject
    public QueryResultCacheHandler(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @GET
    @Path("{queryName}")
    public Response executeQuery(@PathParam("queryName") String queryName) {
        QueryResult queryResult = queryProcessor.placeQuery(() -> decodeString(queryName));

        if (queryResult.hasErrors()) {
            List<String> errors = queryResult.getErrors();
            Optional<String> errorsString = errors.stream().reduce((s, s2) -> s + "\n" + s2);
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).entity(errorsString.get()).build();
        }

        return Response.ok(queryResult.getResult()).build();
    }
}
