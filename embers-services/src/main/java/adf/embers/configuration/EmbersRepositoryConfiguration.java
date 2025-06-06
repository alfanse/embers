package adf.embers.configuration;

import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryExecutor;
import adf.embers.query.impl.QueryExecutorRecordingStatistics;
import adf.embers.query.persistence.QueryDao;
import adf.embers.query.persistence.QueryStatisticsDao;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class EmbersRepositoryConfiguration {

    private final DBI dbi;
    private final DataSource dataSource;

    public EmbersRepositoryConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        dbi = dbi();
    }

    DBI dbi() {
        return new DBI(dataSource);
    }

    public QueryExecutor auditingQueryExecutor() {
        return new QueryExecutorRecordingStatistics(queryExecutor(), queryStatisticsDao());
    }

    public QueryDao queryDao() {
        return dbi.open(QueryDao.class);
    }

    public QueryStatisticsDao queryStatisticsDao() {
        return dbi.open(QueryStatisticsDao.class);
    }

    public QueryResultCacheDao queryResultCacheDao() {
        return dbi.open(QueryResultCacheDao.class);
    }

    public QueryExecutor queryExecutor() {
        return new adf.embers.query.impl.QueryExecutor(dbi);
    }
}
