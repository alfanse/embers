package adf.embers.query.persistence;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Audits result and duration of calls to named queries
 */
public interface QueryStatisticsDao {

    String TABLE_QUERIES_STATISTICS = "queries_statistics";
    String COL_QUERY_NAME = "query_name";
    String COL_DATE_EXECUTED = "date_executed";
    String COL_DURATION = "duration";
    String COL_RESULT = "result";
    String COL_ID = "id";

    @SqlUpdate("insert into " + TABLE_QUERIES_STATISTICS + " ("
            + COL_QUERY_NAME + ", " + COL_DATE_EXECUTED + ", " + COL_DURATION + ", " + COL_RESULT
            + ") values (:aq.name, :aq.dateExecuted, :aq.duration, :aq.result)")
    void save(@BindBean("aq") QueryStatistics queryStatistics);
}
