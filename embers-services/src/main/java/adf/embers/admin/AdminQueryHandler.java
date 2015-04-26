package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response addQuery(Query query) {

        queryDao.save(query);
        return Response.ok("Successfully added query").build();
    }
}
