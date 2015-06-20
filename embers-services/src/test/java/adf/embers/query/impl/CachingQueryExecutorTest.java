package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.persistence.CachedQuery;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryResultCacheDao;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CachingQueryExecutorTest {

    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);
    private final QueryResultCacheDao queryResultCacheDao = mock(QueryResultCacheDao.class);

    private final CachingQueryExecutor cachingQueryExecutor = new CachingQueryExecutor(queryResultCacheDao, queryExecutor);
    private final Query query = new Query();
    private final CachedQuery cachedQuery = new CachedQuery(query, Duration.ofDays(1));
    private final List<Map<String, Object>> realResult = new ArrayList<>();

    @Test
    public void queryOnCacheMissWillCacheResult() throws Exception {

        givenTheCacheResultOf(cachedQuery);

        givenTheLiveQueryResult();

        List<Map<String, Object>> result = whenCachedQueryIsCalled();

        verify(queryResultCacheDao).updateQueryCacheResult(cachedQuery);

        assertThat(result).isSameAs(realResult);
    }

    @Test
    public void queryOnCachedHitWillReuseCachedResult() throws Exception {
        cachedQuery.setCachedQueryResult(realResult);
        givenTheCacheResultOf(cachedQuery);

        List<Map<String, Object>> result = whenCachedQueryIsCalled();

        verify(queryResultCacheDao, Mockito.never()).updateQueryCacheResult(cachedQuery);

        assertThat(result).isSameAs(realResult);
    }

    private List<Map<String, Object>> whenCachedQueryIsCalled() {
        return cachingQueryExecutor.runQuery(query);
    }

    private void givenTheCacheResultOf(CachedQuery cachedQuery) {
        when(queryResultCacheDao.findCachedQueryResult(query)).thenReturn(cachedQuery);
    }

    private void givenTheLiveQueryResult() {
        when(queryExecutor.runQuery(query)).thenReturn(realResult);
    }

}