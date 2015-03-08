package adf.embers.query.impl;

import adf.embers.configuration.DbiHandleFactory;
import adf.embers.query.persistence.Query;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

public class QueryExecutor implements adf.embers.query.QueryExecutor {
    private final DbiHandleFactory handleFactory;

    public QueryExecutor(DbiHandleFactory handleFactory) {
        this.handleFactory = handleFactory;
    }

    @Override
    public List<Map<String, Object>> runQuery(Query query) {
        Handle handle = handleFactory.getHandle();
        return handle.select(query.getSql());
    }
}
