package adf.embers.query.impl;

import adf.embers.query.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class QueryResultBuilder {

    private String result;
    private final ArrayList<String> errors = new ArrayList<>();

    public QueryResultBuilder withResult(String result) {
        this.result = result;
        return this;
    }

    public QueryResultBuilder addError(String error) {
        this.errors.add(error);
        return this;
    }

    public boolean hasNoErrors() {
        return errors.isEmpty();
    }

    public QueryResult build() {
        return new QueryResult() {
            @Override
            public String getResult() {
                return result;
            }

            @Override
            public boolean hasErrors() {
                return errors.size() > 0;
            }

            @Override
            public List<String> getErrors() {
                return errors;
            }
        };
    }
}
