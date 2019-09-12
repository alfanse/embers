package adf.embers.examples.spring;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.cache.QueryResultCacheHandler;
import adf.embers.cache.QueryResultCacheProcessor;
import adf.embers.configuration.EmbersConfiguration;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryProcessor;
import adf.embers.query.persistence.QueryDao;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class EmbersSpringConfiguration extends ResourceConfig {

    public EmbersSpringConfiguration(@Autowired DataSource dataSource) {
        EmbersConfiguration embersConfiguration = new EmbersConfiguration(dataSource);

        register(QueryHandler.class);
        register(AdminQueryHandler.class);
        register(QueryResultCacheHandler.class);

        register(embersConfiguration.getQueryHandler());
        register(embersConfiguration.getAdminQueryHandler());
        register(embersConfiguration.getQueryResultCacheHandler());

        System.out.println("EmbersSpringConfig loaded.");
    }

    public QueryHandler getQueryHandler(@Autowired QueryProcessor queryProcessor) {
        return new QueryHandler(queryProcessor);
    }

    public AdminQueryHandler getAdminQueryHandler(@Autowired QueryDao queryDao) {
        return new AdminQueryHandler(queryDao);
    }

    public QueryResultCacheHandler getQueryResultCacheHandler(@Autowired QueryResultCacheProcessor queryProcessor) {
        return new QueryResultCacheHandler(queryProcessor);
    }
}
