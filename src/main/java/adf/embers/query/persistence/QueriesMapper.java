package adf.embers.query.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueriesMapper implements ResultSetMapper<Query>{

    @Override
    public Query map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Query query = new Query(getString(r, QueriesDao.COL_NAME), getString(r, QueriesDao.COL_DESCRIPTION), getString(r, QueriesDao.COL_SQL));
        query.setId(r.getLong(QueriesDao.COL_ID));
        return query;
    }

    private String getString(ResultSet r, String colName) throws SQLException {
        return r.getString(colName);
    }
}
