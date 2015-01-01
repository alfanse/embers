package adf.embers.configuration;

import adf.embers.query.QueryFormatter;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryRunner;
import adf.embers.query.impl.QueryProcessor;
import adf.embers.query.persistence.QueryDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.util.Collections;

public class EmbersConfiguration {

    private DataSource dataSource;
    private final QueryHandler queryHandler;

    public EmbersConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        final DBI dbi = dbi();
        final QueryDao queryDao = queryDao(dbi);
        final QueryRunner queryRunner = queryRunner();
        final QueryFormatter queryFormatter = queryFormatter();
        final QueryProcessor queryProcessor = queryProcessor(queryDao, queryRunner, queryFormatter);
        this.queryHandler = queryHandler(queryProcessor);
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    private QueryHandler queryHandler(QueryProcessor queryProcessor) {
        return new QueryHandler(queryProcessor);
    }

    private QueryProcessor queryProcessor(QueryDao queryDao, QueryRunner queryRunner, QueryFormatter queryFormatter) {
        return new QueryProcessor(queryDao, queryRunner, queryFormatter);
    }

    private QueryDao queryDao(DBI dbi) {
        return dbi.open(QueryDao.class);
    }

    private DBI dbi() {
        return new DBI(dataSource);
    }

    private QueryFormatter queryFormatter() {
        return (result, queryRequest) -> queryRequest.getQueryName();
    }

    private QueryRunner queryRunner() {
        return query -> Collections.emptyMap();
    }


}
