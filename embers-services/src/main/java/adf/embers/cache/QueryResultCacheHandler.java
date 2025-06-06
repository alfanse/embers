package adf.embers.cache;

import adf.embers.query.QueryProcessor;
import adf.embers.query.QueryResult;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static adf.embers.statics.UrlTools.decodeString;

@Path(QueryResultCacheHandler.PATH)
public class QueryResultCacheHandler {

    public static final String PATH = "/cached";
    public static final String HEADER_WHEN_CHACHED = "REPORT_CACHED_AT";

    private final QueryProcessor queryProcessor;

    @Inject @Caching
    public QueryResultCacheHandler(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @GET
    @Path("{queryName}")
    public Response executeQuery(@PathParam("queryName") String queryName) {
        QueryResult queryResult = queryProcessor.placeQuery(() -> decodeString(queryName));

        if (queryResult.hasErrors()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .entity(queryResult.getErrorMessages())
                    .build();
        }

        return Response
                .ok(queryResult.getResult())
                .header(HEADER_WHEN_CHACHED, queryResult.getCachedOn())
                .type("text/csv")
                .build();
    }

}
