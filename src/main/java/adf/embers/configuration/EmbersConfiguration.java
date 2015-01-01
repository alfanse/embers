package adf.embers.configuration;

import adf.embers.query.QueryHandler;
import adf.embers.query.impl.QueryProcessor;
import adf.embers.query.persistence.QueriesDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private DataSource dataSource;
    private final QueryHandler queryHandler;

    public EmbersConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        DBI dbi = dbi();
        QueriesDao queriesDao = queriesDao(dbi);
        QueryProcessor queryProcessor = queryProcessor(queriesDao);
        queryHandler = queryHandler(queryProcessor);
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    private QueryHandler queryHandler(QueryProcessor queryProcessor) {
        return new QueryHandler(queryProcessor);
    }

    private QueryProcessor queryProcessor(QueriesDao queriesDao) {
        return new QueryProcessor(queriesDao);
    }

    private QueriesDao queriesDao(DBI dbi) {
        return dbi.open(QueriesDao.class);
    }

    private DBI dbi() {
        return new DBI(dataSource);
    }


}
