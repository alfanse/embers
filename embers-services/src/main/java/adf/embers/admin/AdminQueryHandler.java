package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

        queryDao.save(new Query(
                decodeString(query.getName()),
                decodeString(query.getDescription()),
                decodeString(query.getSql())
        ));

        return Response.ok("Successfully added query").build();
    }

    private String decodeString(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to decode : " + encodedString);
        }
    }
}
