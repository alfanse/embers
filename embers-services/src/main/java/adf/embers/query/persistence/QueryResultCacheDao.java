package adf.embers.query.persistence;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(QueryResultCacheMapper.class)
public interface QueryResultCacheDao {
    String TABLE_QUERIES_RESULT_CACHE = "Query_Result_Cache";
    String COL_ID = "query_result_cache_id";
    String COL_QUERY_NAME = "query_name";
    String COL_LIVE_DURATION_MS = "live_duration_ms";
    String COL_DATE_CACHED = "date_cached";
    String COL_RESULT = "result";

    @SqlUpdate("insert into " + TABLE_QUERIES_RESULT_CACHE + " ("
            + COL_QUERY_NAME + ", " + COL_LIVE_DURATION_MS
            + ") values (:cq.queryName, :cq.liveDurationMs)")
    void save(@BindBean("cq") CachedQuery cachedQuery);

    @SqlQuery("select * from " + TABLE_QUERIES_RESULT_CACHE + " where " + COL_QUERY_NAME + " = :q.name")
    CachedQuery findCachedQueryResult(@BindBean("q") Query query);

    @SqlUpdate("update "+TABLE_QUERIES_RESULT_CACHE
               + " set " + COL_DATE_CACHED + "= :cq.dateCached, "
                         + COL_RESULT+ "= :cq.cachedQueryResult " +
               " where " + COL_QUERY_NAME +"= :cq.queryName")
    void updateQueryCacheResult(@BindBean("cq") CachedQuery cachedQuery);
}
