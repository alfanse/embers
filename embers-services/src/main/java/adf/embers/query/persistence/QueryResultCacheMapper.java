package adf.embers.query.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryResultCacheMapper implements ResultSetMapper<CachedQuery> {
    @Override
    public CachedQuery map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        CachedQuery cachedQuery = new CachedQuery(r.getString(QueryResultCacheDao.COL_QUERY_NAME), r.getLong(QueryResultCacheDao.COL_LIVE_DURATION_MS));
        cachedQuery.setId(r.getLong(QueryResultCacheDao.COL_ID));
        cachedQuery.setDateCached(r.getDate(QueryResultCacheDao.COL_DATE_CACHED));

        Clob clob = r.getClob(QueryResultCacheDao.COL_RESULT);
        if(clob!=null) {
            cachedQuery.setCachedQueryResult(new ArrayList<>());
        }
        return cachedQuery;
    }
}
