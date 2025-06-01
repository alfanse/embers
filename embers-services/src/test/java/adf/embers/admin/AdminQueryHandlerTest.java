package adf.embers.admin;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import jakarta.ws.rs.core.Response;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.net.HttpURLConnection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AdminQueryHandlerTest {

    private static final String PLAIN_QUERY_NAME = "new query";
    private static final String ENCODED_QUERY_NAME = "new+query";
    private static final String PLAIN_DESCRIPTION = "this encoded description, should change";
    private static final String PLAIN_SQL = "select systimestamp from dual";
    private final QueryDao queryDao = mock(QueryDao.class);

    private final AdminQueryHandler adminQueryHandler = new AdminQueryHandler(queryDao);
    private Query postedQuery;

    @Test
    public void saveRetunsOkWithSuccessMessage() {

        this.postedQuery = new Query("newQuery", "Description", "select timestamp from dual");

        Response response = whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        verify(queryDao).save(any(Query.class));

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.getEntity().toString()).contains("Successfully added");
    }

    @Test
    public void saveWillUseDecodedQuerySql() {
        this.postedQuery = encodedQuery();
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getSql()).isEqualTo(PLAIN_SQL);
    }

    @Test
    public void saveWillUseDecodedQueryDescription() {
        this.postedQuery = encodedQuery();
        whenAdminQueryHandlerAddQueryIsCalled(postedQuery);

        final ArgumentCaptor<Query> argumentCaptor = ArgumentCaptor.forClass(Query.class);
        verify(queryDao).save(argumentCaptor.capture());

        Assertions.assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(PLAIN_DESCRIPTION);
    }

    @Test
    public void saveWillUseDecodedQueryName() {
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
        return adminQueryHandler.addOrUpdateQuery(postedQuery);
    }

}