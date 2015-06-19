package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryResultCacheDao;
import adf.embers.query.persistence.ResultHolder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachingQueryExecutorTest {

    private final QueryExecutor queryExecutor = mock(QueryExecutor.class);
    private final QueryResultCacheDao queryResultCacheDao = mock(QueryResultCacheDao.class);

    private final CachingQueryExecutor cachingQueryExecutor = new CachingQueryExecutor(queryResultCacheDao, queryExecutor);

    @Test
    public void queryOnCacheMissCachesResult() throws Exception {
        Query query = new Query();

        when(queryResultCacheDao.findCachedResult(query)).thenReturn(new ResultHolder());

        ArrayList<Map<String, Object>> realResult = new ArrayList<>();
        when(queryExecutor.runQuery(query)).thenReturn(realResult);

        cachingQueryExecutor.runQuery(query);

    }

}