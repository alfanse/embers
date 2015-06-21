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

        CachedQuery cachedQueryResult = queryResultCacheDao.findCachedQueryResult(queryRequest);

        if(hasNeverBeenCachedBefore(cachedQueryResult)) {

            Query query = queryDao.findQueryByName(queryRequest.getQueryName());

            if(isNotACachableQuery(query)){

                queryResultBuilder.addError(
                        (query == null ? "Query not found: " : "Query not eligible for caching: ")
                        +queryRequest.getQueryName());

            } else {

                List<Map<String, Object>> queryResult = queryExecutor.runQuery(query);

                cachedQueryResult = new CachedQuery(query.getName(), query.getCacheableDuration().toMillis());
                cachedQueryResult.setCachedQueryResult(queryResult);
                cachedQueryResult.setDateCached(new Date());
                queryResultCacheDao.save(cachedQueryResult);

            }
        }

        //todo cache expired

        //todo cache re-useable

        if(hasResultThatNeedsFormatting(queryResultBuilder, cachedQueryResult)) {
            final String formattedResult = new CsvFormatter().format(cachedQueryResult.getCachedQueryResult());
            queryResultBuilder.withResult(formattedResult);
        }

        return queryResultBuilder.build();
    }

    private boolean hasResultThatNeedsFormatting(QueryResultBuilder queryResultBuilder, CachedQuery cachedQueryResult) {
        return queryResultBuilder.hasNoErrors() && cachedQueryResult.hasCachedQueryResult();
    }

    private boolean isNotACachableQuery(Query query) {
        return query == null || query.getCacheableDuration() == null;
    }

    private boolean hasNeverBeenCachedBefore(CachedQuery cachedQueryResult) {
        return cachedQueryResult == null;
    }
}
