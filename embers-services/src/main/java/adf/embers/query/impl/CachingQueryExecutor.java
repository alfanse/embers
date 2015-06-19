package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryResultCacheDao;

import java.util.List;
import java.util.Map;

public class CachingQueryExecutor implements adf.embers.query.QueryExecutor {

    private QueryResultCacheDao queryResultCacheDao;
    private QueryExecutor queryExecutor;

    public CachingQueryExecutor(QueryResultCacheDao queryResultCacheDao, QueryExecutor queryExecutor) {
        this.queryResultCacheDao = queryResultCacheDao;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public List<Map<String, Object>> runQuery(Query query) {
        return null;
    }
}
