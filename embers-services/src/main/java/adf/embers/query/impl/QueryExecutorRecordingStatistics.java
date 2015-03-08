package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryStatistics;
import adf.embers.query.persistence.QueryStatisticsDao;

import java.util.List;
import java.util.Map;

public class QueryExecutorRecordingStatistics implements QueryExecutor {
    private QueryExecutor queryExecutor;
    private QueryStatisticsDao auditQueryDao;

    public QueryExecutorRecordingStatistics(QueryExecutor queryExecutor, QueryStatisticsDao auditQueryDao) {
        this.queryExecutor = queryExecutor;
        this.auditQueryDao = auditQueryDao;
    }

    @Override
    public List<Map<String, Object>> runQuery(Query query) {
        final QueryStatistics queryStatistics = new QueryStatistics(query);
        final List<Map<String, Object>> result = this.queryExecutor.runQuery(query);
        queryStatistics.setDuration();
        queryStatistics.setResult("Number of rows returned: " + result.size());
        auditQueryDao.save(queryStatistics);
        return result;
    }
}
