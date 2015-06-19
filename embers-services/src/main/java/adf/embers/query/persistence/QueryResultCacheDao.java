package adf.embers.query.persistence;

public interface QueryResultCacheDao {
    String TABLE_QUERIES_RESULT_CACHE = "Query_Result_Cache";
    String COL_ID = "query_result_cache_id";

    ResultHolder findCachedResult(Query query);
}
