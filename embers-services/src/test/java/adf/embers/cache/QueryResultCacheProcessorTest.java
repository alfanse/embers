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
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class QueryResultCacheProcessorTest {

    public static final String QUERY_NAME = "queryName";
    private final QueryResultCacheDao queryResultCacheDao = mock(QueryResultCacheDao.class);
    private final QueryDao queryDao = mock(QueryDao.class);
    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);

    private final QueryResultCacheProcessor queryResultCacheProcessor = new QueryResultCacheProcessor(queryResultCacheDao, queryDao, queryExecutor);

    private final QueryRequest queryRequest = () -> QUERY_NAME;
    private final Query query = mock(Query.class);
    private final CachedQuery cachedQuery = mock(CachedQuery.class);

    @Test
    public void noCacheRecordAndNoQuery() throws Exception {
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);
        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(null);

        QueryResult queryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryResultCacheDao, never()).updateQueryCacheResult(any(CachedQuery.class));

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not found: " + QUERY_NAME);
    }

    @Test
    public void noCacheRecordAndQueryExistsButNoCacheDurationSet() throws Exception {
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);
        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(query);
        when(query.getCacheableDuration()).thenReturn(null);

        QueryResult queryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryResultCacheDao, never()).updateQueryCacheResult(any(CachedQuery.class));

        assertThat(queryResult.hasErrors()).isTrue();
        assertThat(queryResult.getErrors()).contains("Query not eligible for caching: " + QUERY_NAME);
    }

    @Test
    public void noCacheRecordAndQueryExistsAndCanBeCached() throws Exception {
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(null);

        Duration cachableDuration = givenAQueryWithACachableDuration();
        List<Map<String, Object>> queryResult = givenTheResultOfExecutingAQuery();

        QueryResult cachedQueryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        ArgumentCaptor<CachedQuery> saveArgumentCaptor = ArgumentCaptor.forClass(CachedQuery.class);
        verify(queryResultCacheDao).save(saveArgumentCaptor.capture());

        CachedQuery savedCachedQuery = saveArgumentCaptor.getValue();
        assertThat(savedCachedQuery.getQueryName()).isEqualTo(QUERY_NAME);
        assertThat(savedCachedQuery.getLiveDurationMs()).isEqualTo(cachableDuration.toMillis());
        //mockito has same instance captured twice, i.e. update has already happened on this instance
//        assertThat(savedCachedQuery.getCachedQueryResult()).isNull();
//        assertThat(savedCachedQuery.getTimestampWhenCached()).isNull();

        ArgumentCaptor<CachedQuery> updateArgumentCaptor = ArgumentCaptor.forClass(CachedQuery.class);
        verify(queryResultCacheDao).updateQueryCacheResult(updateArgumentCaptor.capture());

        CachedQuery updatedCachedQuery = updateArgumentCaptor.getValue();
        assertThat(updatedCachedQuery.getQueryName()).isEqualTo(QUERY_NAME);
        assertThat(updatedCachedQuery.getLiveDurationMs()).isEqualTo(cachableDuration.toMillis());
        assertThat(updatedCachedQuery.getCachedQueryResult()).isEqualTo(queryResult);
        assertThat(updatedCachedQuery.getTimestampWhenCached()).isNotNull();
        //hmmm not sure mockito ArgumentCapture is playing fair.
        assertThat(savedCachedQuery).isSameAs(updatedCachedQuery);

        assertThat(cachedQueryResult.hasErrors()).isFalse();
        assertThat(cachedQueryResult.getResult()).isEmpty();
    }

    @Test
    public void cachedRecordAndWithinLiveDuration() throws Exception {
        when(cachedQuery.hasCachedQueryResult()).thenReturn(true);
        when(cachedQuery.getCachedQueryResult()).thenReturn(emptyList());

        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(cachedQuery);
        when(cachedQuery.isCacheMiss()).thenReturn(false);

        QueryResult queryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryDao, never()).findQueryByName(QUERY_NAME);
        verify(queryResultCacheDao, never()).updateQueryCacheResult(any(CachedQuery.class));

        assertThat(queryResult.hasErrors()).isFalse();
        assertThat(queryResult.getResult()).contains("");
    }

    @Test
    public void cachedRecordButExpired() throws Exception {
        when(queryResultCacheDao.findCachedQueryResult(queryRequest)).thenReturn(cachedQuery);
        when(cachedQuery.isCacheMiss()).thenReturn(true);
        when(cachedQuery.hasCachedQueryResult()).thenReturn(true);
        when(cachedQuery.getCachedQueryResult()).thenReturn(emptyList());

        Duration cachableDuration = givenAQueryWithACachableDuration();
        List<Map<String, Object>> queryResult = givenTheResultOfExecutingAQuery();

        QueryResult cachedQueryResult = queryResultCacheProcessor.placeQuery(queryRequest);

        verify(queryResultCacheDao, never()).save(any(CachedQuery.class));

        verify(queryResultCacheDao).updateQueryCacheResult(Mockito.eq(cachedQuery));

        verify(cachedQuery).setLiveDurationMs(cachableDuration.toMillis());
        verify(cachedQuery).setCachedQueryResult(queryResult);
        verify(cachedQuery).setDateWhenCached(any(Date.class));

        assertThat(cachedQueryResult.hasErrors()).isFalse();
        assertThat(cachedQueryResult.getResult()).isEmpty();
    }

    private Duration givenAQueryWithACachableDuration() {
        when(queryDao.findQueryByName(QUERY_NAME)).thenReturn(query);

        when(query.getName()).thenReturn(QUERY_NAME);
        Duration cachableDuration = Duration.ofHours(1);
        when(query.getCacheableDuration()).thenReturn(cachableDuration);
        return cachableDuration;
    }

    private List<Map<String, Object>> givenTheResultOfExecutingAQuery() {
        List<Map<String, Object>> queryResult = emptyList();
        when(queryExecutor.runQuery(query)).thenReturn(queryResult);
        return queryResult;
    }

}