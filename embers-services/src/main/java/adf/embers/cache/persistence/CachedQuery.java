package adf.embers.cache.persistence;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CachedQuery {

    private String queryName;
    private long liveDurationMs;
    private Timestamp timestampWhenCached;
    private List<Map<String, Object>> result;
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

    public void setLiveDurationMs(long liveDurationMs) {
        this.liveDurationMs = liveDurationMs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isCacheMiss() {
        return result == null || hasCacheExpired();
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> queryResult) {
        this.result = queryResult;
    }

    /**
     * Called by QueryResultCacheDao.updateQueryCacheResult
     */
    @SuppressWarnings("unused")
    public String getCachedQueryResultAsJsonString() {
        return new QueryResultToClobConverter().serialise(result);
    }

    public Timestamp getTimestampWhenCached() {
        return timestampWhenCached;
    }

    public void setTimestampWhenCached(Timestamp timestampWhenCached) {
        this.timestampWhenCached = timestampWhenCached;
    }

    public void setDateWhenCached(Date dateWhenCached) {
        setTimestampWhenCached(new Timestamp(dateWhenCached.getTime()));
    }

    public boolean hasCachedQueryResult() {
        return result != null;
    }

    private boolean hasCacheExpired() {
        return new Date().after(caclulateCacheExpirationTime());
    }

    private Date caclulateCacheExpirationTime() {
        return new CacheExpiryDateCalculator(timestampWhenCached, liveDurationMs).invoke();
    }

}
