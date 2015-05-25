package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static adf.embers.statics.UrlTools.decodeString;

@Path(AdminQueryHandler.PATH)
//taken from http://docs.oracle.com/javaee/6/tutorial/doc/gkknj.html
public class AdminQueryHandler {
    public static final String PATH = "/admin";
    private QueryDao queryDao;

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

        return Response.ok("Successfully " + strategy + " query").build();
    }

    @DELETE
    @Path("{queryName}")
    public Response deleteQuery(@PathParam("queryName") String queryName){
        queryDao.delete(decodeString(queryName));
        return Response.ok("Successfully deleted query").build();
    }

    private Query createDecodedQuery(Query query) {
        return new Query(
                decodeString(query.getName()),
                decodeString(query.getDescription()),
                decodeString(query.getSql())
        );
    }

}
