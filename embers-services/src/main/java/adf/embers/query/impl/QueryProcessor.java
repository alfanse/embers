package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.QueryFormatter;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.impl.formatters.CsvFormatter;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import java.util.List;
import java.util.Map;

/** Loads the Query, executes it, returns result */
public class QueryProcessor implements adf.embers.query.QueryProcessor {

    private final QueryDao queryDao;
    private final QueryExecutor queryExecutor;

    public QueryProcessor(QueryDao queryDao, QueryExecutor queryExecutor) {
        this.queryDao = queryDao;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public QueryResult placeQuery(final QueryRequest queryRequest) {
        QueryResultBuilder queryResultBuilder = new QueryResultBuilder();
        try {
            Query query = queryDao.findQueryByName(queryRequest.getQueryName());
            if(query==null) {
                queryResultBuilder.addError("Query not found: " + queryRequest.getQueryName());
            } else {
                executeNamedQueryAndFormatResult(queryResultBuilder, query);
            }
        } catch (Exception e) {
            queryResultBuilder.addError(e.getMessage());
        }

        return queryResultBuilder.build();
    }

    private void executeNamedQueryAndFormatResult(QueryResultBuilder queryResultBuilder, Query query) {
        List<Map<String, Object>> result = queryExecutor.runQuery(query);
        final String formattedResult = getFormatter().format(result);
        queryResultBuilder.withResult(formattedResult);
    }

    private QueryFormatter getFormatter() {
        return new CsvFormatter();
    }
}
