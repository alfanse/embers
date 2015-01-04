package adf.embers.configuration;

import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryHandler;
import adf.embers.query.impl.QueryProcessor;
import adf.embers.query.persistence.QueryDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private DataSource dataSource;
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
        return new QueryProcessor(queryDao(dbi), queryRunner());
    }

    private QueryDao queryDao(DBI dbi) {
        return dbi.open(QueryDao.class);
    }

    private QueryExecutor queryRunner() {
        return new adf.embers.query.impl.QueryExecutor(dbiFactory());
    }

    private DbiFactory dbiFactory() {
        return new DbiFactory(dbi);
    }


}
