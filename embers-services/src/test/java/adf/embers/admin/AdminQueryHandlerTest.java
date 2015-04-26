package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AdminQueryHandlerTest {

    private final QueryDao queryDao = mock(QueryDao.class);

    private final AdminQueryHandler adminQueryHandler = new AdminQueryHandler(queryDao);
    private Query postedQuery;

    @Test
    public void successfullSaveRetunsOk() throws Exception {

        this.postedQuery = new Query("newQuery", "Description", "select timestamp from dual");

        Response response = whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        verify(queryDao).save(any(Query.class));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
    }

    @Test
    public void querySqlIsDecoded() throws Exception {
        this.postedQuery = new Query("newQuery", "Description", "select+systimestamp+from+dual");
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getSql()).isEqualTo("select systimestamp from dual");
    }

    @Test
    public void queryDescriptionIsDecoded() throws Exception {
        this.postedQuery = new Query("newQuery", "this+encoded+description,+should+change", "select+systimestamp+from+dual");
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        Assertions.assertThat(argumentCaptor.getValue().getDescription()).isEqualTo("this encoded description, should change");
    }

    @Test
    public void queryNameIsDecoded() throws Exception {
        this.postedQuery = new Query("new+query", "this+encoded+description,+should+change", "select+systimestamp+from+dual");
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getName()).isEqualTo("new query");
    }

    private Response whenAdminQueryHandlerAddQueryIsCalled(Query postedQuery) {
        return adminQueryHandler.addQuery(postedQuery);
    }

}