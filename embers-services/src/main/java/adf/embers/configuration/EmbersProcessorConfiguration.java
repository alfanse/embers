package adf.embers.configuration;

import adf.embers.cache.QueryResultCacheProcessor;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryProcessor;
import adf.embers.query.persistence.QueryDao;

public class EmbersProcessorConfiguration {

    private final QueryDao queryDao;
    private final QueryExecutor queryExecutor;
    private final QueryResultCacheDao queryResultCacheDao;

    public EmbersProcessorConfiguration(
            QueryDao queryDao,
            QueryExecutor queryExecutor,
            QueryResultCacheDao queryResultCacheDao) {
        this.queryDao = queryDao;
        this.queryExecutor = queryExecutor;
        this.queryResultCacheDao = queryResultCacheDao;
    }

    public EmbersProcessorConfiguration(EmbersRepositoryConfiguration embersRepositoryConfiguration) {
        this(embersRepositoryConfiguration.queryDao(), embersRepositoryConfiguration.auditingQueryExecutor(), embersRepositoryConfiguration.queryResultCacheDao());
    }

    public QueryResultCacheProcessor cachedQueryProcessor() {
        return new QueryResultCacheProcessor(queryResultCacheDao, queryDao, queryExecutor);
    }

    public QueryProcessor queryProcessor() {
        return new adf.embers.query.impl.QueryProcessor(queryDao, queryExecutor);
    }


    public QueryDao queryDao() {
        return this.queryDao;
    }
}
