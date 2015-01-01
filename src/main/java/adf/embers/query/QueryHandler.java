package adf.embers.query;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

@Path("/" + QueryHandler.PATH)
public class QueryHandler {

    public static final String PATH = "query";

    private final QueryProcessor queryProcessor;

    @Inject
    public QueryHandler(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @GET
    @Path("{queryName}")
    public Response executeQuery(@PathParam("queryName") String queryName) {
        QueryResult queryResult = queryProcessor.placeQuery(() -> queryName);
        if (queryResult.hasErrors()) {
            //todo lots of types of errors nicely translated and reported on.
            List<String> errors = queryResult.getErrors();
            Optional<String> errorsString = errors.stream().reduce((s, s2) -> s + "\n" + s2);
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).entity(errorsString.get()).build();
//            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).type(MediaType.TEXT_PLAIN_TYPE).entity(errorsString.get()).build();
        }
        return Response.ok(queryResult.getResult()).build();
    }
}
