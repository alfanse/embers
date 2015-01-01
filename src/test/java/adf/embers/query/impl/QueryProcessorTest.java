package adf.embers.query.impl;

import adf.embers.query.QueryResult;
import adf.embers.query.impl.QueryProcessor;
import adf.embers.query.persistence.QueriesDao;
import adf.embers.query.persistence.Query;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryProcessorTest {

    private final QueriesDao queriesDao = mock(QueriesDao.class);
    private final QueryProcessor queryProcessor = new QueryProcessor(queriesDao);

    @Test
    public void requestedQueryNotFound() {
        when(queriesDao.findAll()).thenReturn(Collections.<Query>emptyList());

        QueryResult queryResult = queryProcessor.placeQuery(() -> "notFoundName");

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not found: notFoundName");
    }

    @Test
    public void daoExceptionHandledAsError() {
        when(queriesDao.findAll()).thenThrow(new RuntimeException("user lacks privilege or object not found: QUERIES"));

        QueryResult queryResult = queryProcessor.placeQuery(() -> "aQuery");
        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("user lacks privilege or object not found: QUERIES");
    }

    @Test
    public void findAndRunQueryReturningResult() {
        //todo
    }

}