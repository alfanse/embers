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
import static org.mockito.Mockito.*;

public class AdminQueryHandlerTest {

    public static final String PLAIN_QUERY_NAME = "new query";
    public static final String ENCODED_QUERY_NAME = "new+query";
    public static final String PLAIN_DESCRIPTION = "this encoded description, should change";
    public static final String PLAIN_SQL = "select systimestamp from dual";
    private final QueryDao queryDao = mock(QueryDao.class);

    private final AdminQueryHandler adminQueryHandler = new AdminQueryHandler(queryDao);
    private Query postedQuery;

    @Test
    public void saveRetunsOkWithSuccessMessage() throws Exception {

        this.postedQuery = new Query("newQuery", "Description", "select timestamp from dual");

        Response response = whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        verify(queryDao).save(any(Query.class));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.getEntity().toString()).contains("Successfully added");
    }

    @Test
    public void saveWillUseDecodedQuerySql() throws Exception {
        this.postedQuery = encodedQuery();
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getSql()).isEqualTo(PLAIN_SQL);
    }

    @Test
    public void saveWillUseDecodedQueryDescription() throws Exception {
        this.postedQuery = encodedQuery();
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        Assertions.assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(PLAIN_DESCRIPTION);
    }

    @Test
    public void saveWillUseDecodedQueryName() throws Exception {
        this.postedQuery = encodedQuery();
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getName()).isEqualTo("new query");
    }

    @Test
    public void updateAnExistingQueryUsesDecodedValues(){
        this.postedQuery = encodedQuery();
        when(queryDao.findQueryByName(PLAIN_QUERY_NAME)).thenReturn(this.postedQuery);

        Response response = whenAdminQueryHandlerAddQueryIsCalled(postedQuery);
        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);

        verify(queryDao).update(argumentCaptor.capture());
        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.getEntity().toString()).contains("Successfully updated");

        assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(PLAIN_DESCRIPTION);
        assertThat(argumentCaptor.getValue().getSql()).isEqualTo(PLAIN_SQL);
    }

    private Query encodedQuery() {
        return new Query(ENCODED_QUERY_NAME, "this+encoded+description,+should+change", "select+systimestamp+from+dual");
    }

    private Response whenAdminQueryHandlerAddQueryIsCalled(Query postedQuery) {
        return adminQueryHandler.addQuery(postedQuery);
    }

}