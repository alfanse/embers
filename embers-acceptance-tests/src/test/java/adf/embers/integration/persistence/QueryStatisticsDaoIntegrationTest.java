package adf.embers.integration.persistence;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryStatistics;
import adf.embers.query.persistence.QueryStatisticsDao;
import adf.embers.tools.EmbersDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

import static adf.embers.query.persistence.QueryStatisticsDao.TABLE_QUERIES_STATISTICS;
import static org.fest.assertions.api.Assertions.assertThat;

public class QueryStatisticsDaoIntegrationTest {

    private static EmbersDatabase embersDatabase;
    private QueryStatisticsDao auditQueryDao;

    @BeforeAll
    public static void setupDB() throws Exception {
        embersDatabase = new EmbersDatabase("jdbc:hsqldb:mem:daoTest");
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueriesStatistics();
    }

    @BeforeEach
    public void setupDao() {
        DBI dbi = new DBI(embersDatabase.getDataSource());
        auditQueryDao = dbi.open(QueryStatisticsDao.class);
    }

    @Test
    public void savesAnAuditQuery() throws Exception {
        Query expectedQuery = new Query("testq", "some description", "select 1 from dual");
        final QueryStatistics queryStatistics = new QueryStatistics(expectedQuery);
        queryStatistics.setResult("result");
        auditQueryDao.save(queryStatistics);

        Handle handle = new DBI(embersDatabase.getDataSource()).open();
        final List<Map<String, Object>> result = handle.select("select * from " + TABLE_QUERIES_STATISTICS + " order by id desc");
        assertThat(result).hasSize(1);
        final Map<String, Object> firstRow = result.get(0);
        assertThat(firstRow.get(QueryStatisticsDao.COL_QUERY_NAME)).isEqualTo(queryStatistics.getName());
        assertThat(firstRow.get(QueryStatisticsDao.COL_DATE_EXECUTED)).isEqualTo(queryStatistics.getDateExecuted());
        assertThat(firstRow.get(QueryStatisticsDao.COL_RESULT)).isEqualTo(queryStatistics.getResult());
        assertThat(firstRow.get(QueryStatisticsDao.COL_DURATION)).isInstanceOf(Long.class);
        assertThat(firstRow.get(QueryStatisticsDao.COL_ID)).isInstanceOf(Integer.class);
    }
}