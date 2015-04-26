package adf.embers.configuration;

import adf.embers.admin.AdminQueryHandler;
import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryHandler;
import adf.embers.query.QueryProcessor;
import adf.embers.query.impl.QueryExecutorRecordingStatistics;
import adf.embers.query.persistence.QueryDao;
import adf.embers.query.persistence.QueryStatisticsDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersConfiguration {

    private final DataSource dataSource;
    private final DBI dbi;

    public EmbersConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dbi = dbi();
    }

    public QueryHandler getQueryHandler() {
        return queryHandler();
    }

    public AdminQueryHandler getAdminQueryHandler() {
        return new AdminQueryHandler(queryDao());
    }

    private DBI dbi() {
        return new DBI(dataSource);
    }

    private QueryHandler queryHandler() {
        return new QueryHandler(queryProcessor());
    }

    private QueryProcessor queryProcessor() {
        return new adf.embers.query.impl.QueryProcessor(queryDao(), auditingQueryExecutor());
    }

    private QueryExecutor auditingQueryExecutor() {
        return new QueryExecutorRecordingStatistics(queryExecutor(), queriesExecutedDao());
    }

    private QueryExecutor queryExecutor() {
        return new adf.embers.query.impl.QueryExecutor(dbiFactory());
    }

    private DbiHandleFactory dbiFactory() {
        return new DbiHandleFactory(dbi);
    }

    private QueryDao queryDao() {
        return dbi.open(QueryDao.class);
    }


    private QueryStatisticsDao queriesExecutedDao() {
        return dbi.open(QueryStatisticsDao.class);
    }
}
