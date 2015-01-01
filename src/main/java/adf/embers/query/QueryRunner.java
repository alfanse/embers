package adf.embers.query;

import adf.embers.query.persistence.Query;

import java.util.Map;

public interface QueryRunner {
    Map<String,Object> runQuery(Query query);
}
