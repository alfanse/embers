package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryResult;
import adf.embers.query.persistence.QueryDao;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryProcessorTest {

    private final QueryDao queryDao = mock(QueryDao.class);
    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);
    private final QueryProcessor queryProcessor = new QueryProcessor(queryDao, queryExecutor);

    @Test
    public void requestedQueryNotFound() {
        final String queryName = "notFoundName";
        when(queryDao.findQueryByName(queryName)).thenReturn(null);

        QueryResult queryResult = whenPlaceQueryIsCalled(queryName);

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not found: notFoundName");
    }

    @Test
    public void daoExceptionHandledAsError() {
        final String queryName = "someName";
        when(queryDao.findQueryByName(queryName)).thenThrow(new RuntimeException("user lacks privilege or object not found: QUERIES"));

        QueryResult queryResult = whenPlaceQueryIsCalled(queryName);
        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("user lacks privilege or object not found: QUERIES");
    }

    private QueryResult whenPlaceQueryIsCalled(String queryName) {
        return queryProcessor.placeQuery(() -> queryName);
    }

}