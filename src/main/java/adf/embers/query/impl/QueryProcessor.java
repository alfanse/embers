package adf.embers.query.impl;

import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import adf.embers.query.persistence.QueriesDao;
import adf.embers.query.persistence.Query;

import java.util.List;
import java.util.Optional;

public class QueryProcessor implements adf.embers.query.QueryProcessor {

    private final QueriesDao queriesDao;

    public QueryProcessor(QueriesDao queriesDao) {
        this.queriesDao = queriesDao;
    }

    @Override
    public QueryResult placeQuery(final QueryRequest queryRequest) {
        QueryResultBuilder queryResultBuilder = new QueryResultBuilder();
        List<Query> all;
        try {
            //todo use findQueryByName
            all = queriesDao.findAll();
        } catch (Exception e) {
            return queryResultBuilder.addError(e.getMessage()).build();
        }

        Optional<Query> theRequiredQuery = all.stream()
                .filter(query -> query.getName().equals(queryRequest.getQueryName()))
                .findFirst();

        if (!theRequiredQuery.isPresent()) {
            return queryResultBuilder.addError("Query not found: " + queryRequest.getQueryName()).build();
        }

        return queryResultBuilder.withResult(queryRequest.getQueryName()).build();
    }
}
