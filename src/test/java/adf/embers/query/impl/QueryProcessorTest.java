package adf.embers.query.impl;

import adf.embers.query.QueryResult;
import adf.embers.query.persistence.QueryDao;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryProcessorTest {

    private final QueryDao queriesDao = mock(QueryDao.class);
    private final QueryProcessor queryProcessor = new QueryProcessor(queriesDao);

    @Test
    public void requestedQueryNotFound() {
        final String queryName = "notFoundName";
        when(queriesDao.findQueryByName(queryName)).thenReturn(null);

        QueryResult queryResult = queryProcessor.placeQuery(() -> {
            return queryName;
        });

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not found: notFoundName");
    }

    @Test
    public void daoExceptionHandledAsError() {
        final String queryName = "someName";
        when(queriesDao.findQueryByName(queryName)).thenThrow(new RuntimeException("user lacks privilege or object not found: QUERIES"));

        QueryResult queryResult = queryProcessor.placeQuery(() -> queryName);
        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("user lacks privilege or object not found: QUERIES");
    }

    @Test
    @Ignore
    public void findAndRunQueryReturningResult() {
        //todo
    }

}