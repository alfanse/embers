package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static adf.embers.statics.UrlTools.decodeString;

@Path(AdminQueryHandler.PATH)
//taken from http://docs.oracle.com/javaee/6/tutorial/doc/gkknj.html
public class AdminQueryHandler {
    public static final String PATH = "/admin";
    public static final String PATH_PARAM_QUERY_NAME = "queryName";

    private QueryDao queryDao;

    @Inject
    public AdminQueryHandler(QueryDao queryDao) {
        this.queryDao = queryDao;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdateQuery(Query query) {
        Query decodedQuery = createDecodedQuery(query);
        Query queryOnDb = queryDao.findQueryByName(decodedQuery.getName());

        String strategy;
        if(queryOnDb==null) {
            queryDao.save(decodedQuery);
            strategy = "added";
        } else {
            queryDao.update(decodedQuery);
            strategy= "updated";
        }

        return Response.ok(String.format("Successfully %s query: %s", strategy, query.getName())).build();
    }

    @DELETE
    @Path("{" + PATH_PARAM_QUERY_NAME + "}")
    public Response deleteQuery(@PathParam(PATH_PARAM_QUERY_NAME) String queryName){
        queryDao.delete(decodeString(queryName));
        return Response.ok(String.format("Successfully deleted query: %s", queryName)).build();
    }

    private Query createDecodedQuery(Query query) {
        return new Query(
                decodeString(query.getName()),
                decodeString(query.getDescription()),
                decodeString(query.getSql())
        );
    }

}
