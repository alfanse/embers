package adf.embers.query.impl;

import adf.embers.query.QueryFormatter;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.QueryRunner;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import java.util.Map;

public class QueryProcessor implements adf.embers.query.QueryProcessor {

    private final QueryDao queriesDao;
    private QueryRunner queryRunner;
    private QueryFormatter queryFormatter;

    public QueryProcessor(QueryDao queryDao, QueryRunner queryRunner, QueryFormatter queryFormatter) {
        this.queriesDao = queryDao;
        this.queryRunner = queryRunner;
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

        Map<String, Object> result = queryRunner.runQuery(queryOptional);
        final String formattedResult = queryFormatter.format(result, queryRequest);
        queryResultBuilder.withResult(formattedResult);

        return queryResultBuilder.build();
    }
}
