package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryFormatter;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import java.util.List;
import java.util.Map;

public class QueryProcessor implements adf.embers.query.QueryProcessor {

    private final QueryDao queriesDao;
    private QueryExecutor queryExecutor;
    private QueryFormatter queryFormatter;

    public QueryProcessor(QueryDao queryDao, adf.embers.query.QueryExecutor queryExecutor, QueryFormatter queryFormatter) {
        this.queriesDao = queryDao;
        this.queryExecutor = queryExecutor;
        this.queryFormatter = queryFormatter;
    }

    @Override
    public QueryResult placeQuery(final QueryRequest queryRequest) {
        QueryResultBuilder queryResultBuilder = new QueryResultBuilder();
        Query queryOptional;
        try {
            queryOptional = queriesDao.findQueryByName(queryRequest.getQueryName());
            if(queryOptional==null) {
                return queryResultBuilder.addError("Query not found: " + queryRequest.getQueryName()).build();
            }
        } catch (Exception e) {
            return queryResultBuilder.addError(e.getMessage()).build();
        }

        List<Map<String, Object>> result = queryExecutor.runQuery(queryOptional);
        final String formattedResult = queryFormatter.format(result, queryRequest);
        queryResultBuilder.withResult(formattedResult);

        return queryResultBuilder.build();
    }
}
