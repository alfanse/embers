package adf.embers.query.impl;

import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;

public class QueryProcessor implements adf.embers.query.QueryProcessor {

    @Override
    public QueryResult placeQuery(QueryRequest queryRequest) {




        return new QueryResult() {
            @Override
            public String getResult() {
                return queryRequest.getQueryName();
            }

            @Override
            public boolean hasErrors() {
                return false;
            }
        };
    }
}
