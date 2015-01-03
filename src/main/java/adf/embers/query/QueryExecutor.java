package adf.embers.query;

import adf.embers.query.persistence.Query;

import java.util.List;
import java.util.Map;

public interface QueryExecutor {
    List<Map<String, Object>> runQuery(Query query);
}
