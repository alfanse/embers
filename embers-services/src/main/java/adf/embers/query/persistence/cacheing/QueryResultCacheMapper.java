package adf.embers.query.persistence.cacheing;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryResultCacheMapper implements ResultSetMapper<CachedQuery> {

    private final ClobToQueryResult clobToQueryResult;

    QueryResultCacheMapper(ClobToQueryResult clobToQueryResult) {
        this.clobToQueryResult = clobToQueryResult;
    }

    public QueryResultCacheMapper() {
        this(new ClobToQueryResult());
    }

    @Override
    public CachedQuery map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        CachedQuery cachedQuery = new CachedQuery(r.getString(QueryResultCacheDao.COL_QUERY_NAME), r.getLong(QueryResultCacheDao.COL_LIVE_DURATION_MS));
        cachedQuery.setId(r.getLong(QueryResultCacheDao.COL_ID));
        cachedQuery.setDateCached(r.getTimestamp(QueryResultCacheDao.COL_DATE_CACHED));

        cachedQuery.setCachedQueryResult(
                clobToQueryResult.deserialise(
                        r.getClob(QueryResultCacheDao.COL_RESULT)));
        return cachedQuery;
    }
}
