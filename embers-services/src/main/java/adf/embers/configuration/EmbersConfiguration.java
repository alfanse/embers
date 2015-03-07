package adf.embers.configuration;

import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryProcessor;
import adf.embers.query.persistence.QueryDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private final DataSource dataSource;
    private final QueryHandler queryHandler;
    private final DBI dbi;

    public EmbersConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dbi = dbi();
        this.queryHandler = queryHandler();
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    private DBI dbi() {
        return new DBI(dataSource);
    }

    private QueryHandler queryHandler() {
        return new QueryHandler(queryProcessor());
    }

    private QueryProcessor queryProcessor() {
        return new adf.embers.query.impl.QueryProcessor(queryDao(), queryRunner());
    }

    private QueryDao queryDao() {
        return dbi.open(QueryDao.class);
    }

    private QueryExecutor queryRunner() {
        return new adf.embers.query.impl.QueryExecutor(dbiFactory());
    }

    private DbiFactory dbiFactory() {
        return new DbiFactory(dbi);
    }


}
