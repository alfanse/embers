package adf.embers.cache;

import adf.embers.cache.persistence.CachedQuery;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryProcessor;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.impl.QueryResultBuilder;
import adf.embers.query.impl.formatters.CsvFormatter;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Caching
public class QueryResultCacheProcessor implements QueryProcessor {
    private QueryResultCacheDao queryResultCacheDao;
    private QueryDao queryDao;
    private QueryExecutor queryExecutor;

    public QueryResultCacheProcessor(QueryResultCacheDao queryResultCacheDao, QueryDao queryDao, QueryExecutor queryExecutor) {
        this.queryResultCacheDao = queryResultCacheDao;
        this.queryDao = queryDao;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public QueryResult placeQuery(QueryRequest queryRequest) {
        QueryResultBuilder queryResultBuilder = new QueryResultBuilder();

        CachedQuery cachedQuery = queryResultCacheDao.findCachedQueryResult(queryRequest);

        if(hasNeverBeenCachedBefore(cachedQuery)) {

            Query query = findQuery(queryRequest);

            if(isNotACachableQuery(query)){

                return queryResultBuilder.addError(
                        (query == null ?
                                "Query not found: " :
                                "Query not eligible for caching: ")
                                + queryRequest.getQueryName())
                        .build();

            }

            CachedQuery newCachedQuery = new CachedQuery(query.getName(), query.getCacheableDuration().toMillis());
            queryResultCacheDao.save(newCachedQuery);
            cachedQuery = newCachedQuery;

        }

        if(cachedQuery.isCacheMiss()) {
            Query query = findQuery(queryRequest);
            List<Map<String, Object>> queryResult = executeQuery(query);
            cachedQuery.setLiveDurationMs(query.getCacheableDuration().toMillis());
            cachedQuery.setResult(queryResult);
            cachedQuery.setDateWhenCached(new Date());
            queryResultCacheDao.updateQueryCacheResult(cachedQuery);
        }

        if(hasResultThatNeedsFormatting(queryResultBuilder, cachedQuery)) {
            final String formattedResult = new CsvFormatter().format(cachedQuery.getResult());
            queryResultBuilder.withResult(formattedResult);
            queryResultBuilder.withCachedDate(cachedQuery.getTimestampWhenCached());
        }

        return queryResultBuilder.build();
    }

    private Query findQuery(QueryRequest queryRequest) {
        return queryDao.findQueryByName(queryRequest.getQueryName());
    }

    private List<Map<String, Object>> executeQuery(Query query) {
        return queryExecutor.runQuery(query);
    }

    private boolean hasResultThatNeedsFormatting(QueryResultBuilder queryResultBuilder, CachedQuery cachedQueryResult) {
        return queryResultBuilder.hasNoErrors() && cachedQueryResult.hasCachedQueryResult();
    }

    private boolean isNotACachableQuery(Query query) {
        return query == null || query.getCacheableDuration() == null;
    }

    private boolean hasNeverBeenCachedBefore(CachedQuery cachedQuery) {
        return cachedQuery == null;
    }
}
