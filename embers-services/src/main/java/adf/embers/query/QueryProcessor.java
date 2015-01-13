package adf.embers.query;

public interface QueryProcessor {

    /**
     * Find the query in the known reports section - from embers own schema
     * Run the query  - against the specified schema
     * Return the result.
     */
    QueryResult placeQuery(QueryRequest queryRequest);
}
