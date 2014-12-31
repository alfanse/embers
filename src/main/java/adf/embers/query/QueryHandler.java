package adf.embers.query;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/"+QueryHandler.PATH)
public class QueryHandler {

    public static final String PATH = "query";

    private final QueryProcessor queryProcessor;

    @Inject
    public QueryHandler( QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @GET
    @Path("{queryName}")
    public Response executeQuery(@PathParam("queryName") String queryName) {
        QueryResult queryResult = queryProcessor.placeQuery(() -> queryName);
        if(queryResult.hasErrors()){
            //todo lots of types of errors nicely translated and reported on.
            return Response.status(501).entity("Failed to find query: "+queryName).build();
        }
        return Response.ok(queryResult.getResult()).build();
    }
}
