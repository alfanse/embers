package adf.embers.query.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static adf.embers.query.persistence.QueryDao.*;
import static java.time.Duration.ofMillis;

public class QueryMapper implements ResultSetMapper<Query>{

    @Override
    public Query map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Query query = new Query(
                asString(r, COL_NAME),
                asString(r, COL_DESCRIPTION),
                asString(r, COL_SQL),
                ofMillis(r.getLong(COL_CACHEABLE_DURATION)));
        query.setId(r.getLong(COL_ID));
        return query;
    }

    private String asString(ResultSet r, String colName) throws SQLException {
        return r.getString(colName);
    }
}
