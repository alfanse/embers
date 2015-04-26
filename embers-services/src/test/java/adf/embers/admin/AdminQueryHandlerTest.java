package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AdminQueryHandlerTest {

    private final QueryDao queryDao = mock(QueryDao.class);

    private final AdminQueryHandler adminQueryHandler = new AdminQueryHandler(queryDao);

    @Test
    public void useDelegateToAddQueryProcessResult() throws Exception {

        final Query postedQuery = new Query("newQuery", "Description", "select timestamp from dual");
        Response response = adminQueryHandler.addQuery(postedQuery);

        verify(queryDao).save(postedQuery);

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
    }
}