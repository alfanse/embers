package adf.embers.query.persistence.cacheing;

import adf.embers.query.persistence.Query;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CachedQuery {

    private String queryName;
    private long liveDurationMs;
    private Timestamp dateCached;
    private List<Map<String, Object>> cachedQueryResult;
    private long id;

    public CachedQuery(Query query, Duration liveDurationMs) {
        this.queryName = query.getName();
        this.liveDurationMs = liveDurationMs.toMillis();
    }

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

    public boolean isCacheMiss() {
        return cachedQueryResult ==null;
    }

    public List<Map<String, Object>> getCachedQueryResult() {
        return cachedQueryResult;
    }

    public void setCachedQueryResult(List<Map<String, Object>> queryResult) {
        this.cachedQueryResult = queryResult;
    }

    public String getCachedQueryResultAsJsonString(){
        return new Gson().toJson(cachedQueryResult);
    }

    public Timestamp getDateCached() {
        return dateCached;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDateCached(Date dateCached) {
        this.dateCached = new Timestamp(dateCached.getTime());
    }

    public void setDateCached(Timestamp dateCached) {
        this.dateCached = dateCached;
    }
}
