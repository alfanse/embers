package adf.embers.configuration;

import adf.embers.query.QueryHandler;
import adf.embers.query.impl.QueryProcessor;
import adf.embers.query.persistence.QueryDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private DataSource dataSource;
    private final QueryHandler queryHandler;

    public EmbersConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        DBI dbi = dbi();
        final QueryDao queryDao = queryDao(dbi);
        QueryProcessor queryProcessor = queryProcessor(queryDao);
        queryHandler = queryHandler(queryProcessor);
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    private QueryHandler queryHandler(QueryProcessor queryProcessor) {
        return new QueryHandler(queryProcessor);
    }

    private QueryProcessor queryProcessor(QueryDao queryDao) {
        return new QueryProcessor(queryDao);
    }

    private QueryDao queryDao(DBI dbi) {
        return dbi.open(QueryDao.class);
    }

    private DBI dbi() {
        return new DBI(dataSource);
    }


}
