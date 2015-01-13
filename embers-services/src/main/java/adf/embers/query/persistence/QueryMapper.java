package adf.embers.query.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static adf.embers.query.persistence.QueryDao.*;

public class QueryMapper implements ResultSetMapper<Query>{

    @Override
    public Query map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Query query = new Query(getString(r, COL_NAME), getString(r, COL_DESCRIPTION), getString(r, COL_SQL));
        query.setId(r.getLong(COL_ID));
        return query;
    }

    private String getString(ResultSet r, String colName) throws SQLException {
        return r.getString(colName);
    }
}
