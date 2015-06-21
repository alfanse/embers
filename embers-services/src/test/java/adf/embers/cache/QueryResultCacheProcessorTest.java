package adf.embers.cache;


import adf.embers.cache.persistence.CachedQuery;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class QueryResultCacheProcessorTest {

    private final QueryResultCacheDao queryResultCacheDao = mock(QueryResultCacheDao.class);
    private final QueryDao queryDao = mock(QueryDao.class);
    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);
    private final QueryResultCacheProcessor queryResultCacheProcessor = new QueryResultCacheProcessor(queryResultCacheDao, queryDao, queryExecutor);
    private final Query query = mock(Query.class);
    public static final String QUERY_NAME = "queryName";

    @Test
    public void noCacheRecordAndNoQuery() throws Exception {
        QueryRequest queryRequest = () -> QUERY_NAME;
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);
        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(null);

        QueryResult queryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryResultCacheDao, never()).updateQueryCacheResult(any(CachedQuery.class));

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not found: "+QUERY_NAME);
    }

    @Test
    public void noCacheRecordAndQueryNoCacheDurationSet() throws Exception {
        QueryRequest queryRequest = () -> QUERY_NAME;
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);
        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(query);
        when(query.getCacheableDuration()).thenReturn(null);

        QueryResult queryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryResultCacheDao, never()).updateQueryCacheResult(any(CachedQuery.class));

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not eligible for caching: "+ QUERY_NAME);
    }


    @Test
    public void noCacheRecordAndQueryCanBeCached() throws Exception {
        QueryRequest queryRequest = () -> QUERY_NAME;
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);

        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(query);

        when(query.getName()).thenReturn(QUERY_NAME);
        Duration cachableDuration = Duration.ofHours(1);
        when(query.getCacheableDuration()).thenReturn(cachableDuration);

        List<Map<String, Object>> queryResult = Collections.emptyList();
        when(queryExecutor.runQuery(query)).thenReturn(queryResult);

        QueryResult cachedQueryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        ArgumentCaptor<CachedQuery> argumentCaptor = ArgumentCaptor.forClass(CachedQuery.class);
        verify(queryResultCacheDao).save(argumentCaptor.capture());

        verify(queryResultCacheDao, never()).updateQueryCacheResult(argumentCaptor.capture());

        CachedQuery cachedQuery = argumentCaptor.getValue();
        assertThat(cachedQuery.getQueryName()).isEqualTo(QUERY_NAME);
        assertThat(cachedQuery.getCachedQueryResult()).isEqualTo(queryResult);
        assertThat(cachedQuery.getDateCached()).isNotNull();
        assertThat(cachedQuery.getLiveDurationMs()).isEqualTo(cachableDuration.toMillis());

        assertThat(cachedQueryResult.hasErrors()).isFalse();
        assertThat(cachedQueryResult.getResult()).isEmpty();
    }



}