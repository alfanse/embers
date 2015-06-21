package adf.embers.cache.persistence;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CachedQuery {

    private String queryName;
    private long liveDurationMs;
    private Timestamp dateCached;
    private List<Map<String, Object>> cachedQueryResult;
    private long id;

    public CachedQuery(String queryName, long liveDurationMs) {
        this.queryName = queryName;
        this.liveDurationMs = liveDurationMs;
    }

    public String getQueryName() {
        return queryName;
    }

    public long getLiveDurationMs() {
        return liveDurationMs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isCacheMiss() {
        return cachedQueryResult ==null;
    }

    public List<Map<String, Object>> getCachedQueryResult() {
        return cachedQueryResult;
    }

    public void setCachedQueryResult(List<Map<String, Object>> queryResult) {
        this.cachedQueryResult = queryResult;
    }

    /** Called by QueryResultCacheDao.updateQueryCacheResult */
    @SuppressWarnings("unused")
    public String getCachedQueryResultAsJsonString(){
        return new QueryResultToClobConverter().serialise(cachedQueryResult);
    }

    public Timestamp getDateCached() {
        return dateCached;
    }

    public void setDateCached(Date dateCached) {
        this.dateCached = new Timestamp(dateCached.getTime());
    }

    public void setDateCached(Timestamp dateCached) {
        this.dateCached = dateCached;
    }

    public boolean hasCachedQueryResult() {
        return cachedQueryResult!=null;
    }
}
