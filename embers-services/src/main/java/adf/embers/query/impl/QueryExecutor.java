package adf.embers.query.impl;

import adf.embers.query.persistence.Query;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

/** Executes a pre-defined sql query, returns result */
public class QueryExecutor implements adf.embers.query.QueryExecutor {

    private final DBI dbi;

    public QueryExecutor(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public List<Map<String, Object>> runQuery(Query query) {
        try(Handle handle = dbi.open()){
            return handle.select(query.getSql());
        }
    }
}
