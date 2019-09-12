package adf.embers.configuration;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.cache.QueryResultCacheProcessor;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryProcessor;
import adf.embers.query.persistence.QueryDao;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private final EmbersRepositoryConfiguration  embersRepositoryConfiguration;
    private final QueryDao queryDao;
    private final QueryResultCacheDao queryResultCacheDao;
    private final QueryExecutor queryExecutor;

    public EmbersConfiguration(DataSource dataSource) {
        embersRepositoryConfiguration = new EmbersRepositoryConfiguration(dataSource);
        queryDao = embersRepositoryConfiguration.queryDao();
        queryResultCacheDao = embersRepositoryConfiguration.queryResultCacheDao();
        queryExecutor = embersRepositoryConfiguration.auditingQueryExecutor();
    }

    public QueryHandler getQueryHandler() {
        return new QueryHandler(queryProcessor());
    }

    public AdminQueryHandler getAdminQueryHandler() {
        return new AdminQueryHandler(queryDao);
    }

    public QueryResultCacheHandler getQueryResultCacheHandler() {
        return new QueryResultCacheHandler(cachedQueryProcessor());
    }

    private QueryResultCacheProcessor cachedQueryProcessor() {
        return new QueryResultCacheProcessor(queryResultCacheDao, queryDao, queryExecutor);
    }

    private QueryProcessor queryProcessor() {
        return new adf.embers.query.impl.QueryProcessor(queryDao, queryExecutor);
    }


}
