package adf.embers.configuration;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.cache.QueryResultCacheProcessor;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryProcessor;
import adf.embers.query.persistence.QueryDao;

public class EmbersHandlerConfiguration {

    private final QueryProcessor queryProcessor;
    private final QueryProcessor cachedQueryProcessor;
    private final QueryDao queryDao;

    public EmbersHandlerConfiguration(
            QueryDao queryDao,
            QueryProcessor queryProcessor,
            QueryResultCacheProcessor cachedQueryProcessor) {
        this.queryDao = queryDao;
        this.queryProcessor = queryProcessor;
        this.cachedQueryProcessor = cachedQueryProcessor;
    }

    public EmbersHandlerConfiguration(EmbersProcessorConfiguration embersProcessorConfiguration) {
        this(embersProcessorConfiguration.queryDao(), embersProcessorConfiguration.queryProcessor(), embersProcessorConfiguration.cachedQueryProcessor());
    }

    public QueryHandler queryHandler() {
        return new QueryHandler(queryProcessor);
    }

    public AdminQueryHandler adminQueryHandler() {
        return new AdminQueryHandler(queryDao);
    }

    public QueryResultCacheHandler queryResultCacheHandler() {
        return new QueryResultCacheHandler(cachedQueryProcessor);
    }
}
