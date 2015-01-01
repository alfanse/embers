package adf.embers.query.impl;

import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class QueryProcessor implements adf.embers.query.QueryProcessor {

    private final QueryDao queriesDao;

    public QueryProcessor(QueryDao queryDao) {
        this.queriesDao = queryDao;
    }

    @Override
    public QueryResult placeQuery(final QueryRequest queryRequest) {
        QueryResultBuilder queryResultBuilder = new QueryResultBuilder();
        Query queryOptional;
        try {
            queryOptional = queriesDao.findQueryByName(queryRequest.getQueryName());
        } catch (Exception e) {
            return queryResultBuilder.addError(e.getMessage()).build();
        }

        if(queryOptional==null){
            queryResultBuilder.addError("Query not found: " + queryRequest.getQueryName());
        } else {
            queryResultBuilder.withResult(queryRequest.getQueryName());
        }

        return queryResultBuilder.build();
    }
}
