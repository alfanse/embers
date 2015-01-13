package adf.embers.query.impl;

import adf.embers.configuration.DbiFactory;
import adf.embers.query.persistence.Query;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

public class QueryExecutor implements adf.embers.query.QueryExecutor {
    private final DbiFactory dbiFactory;

    public QueryExecutor(DbiFactory dbiFactory) {
        this.dbiFactory = dbiFactory;
    }

    @Override
    public List<Map<String, Object>> runQuery(Query query) {
        Handle handle = dbiFactory.getHandle();
        return handle.select(query.getSql());
    }
}
