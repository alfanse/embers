package adf.embers.query.persistence;

import java.time.Duration;

/** Entity for the Queries Table */
public class Query {

    /** Internal ID of a record */
    private Long id;

    /** Natural key for a query. Used by clients to request the query be executed, must be unique */
    private String name;

    /** Human readable description of the report */
    private String description;

    /** SQL of the query, executed when the query is requested */
    private String sql;

    /** Amount of time this query can be cached for.
     * Optional field, set it to indicate this query can be cached.*/
    private Duration cacheableDuration;

    public Query() {
        //default needed for jaxson
    }

    public Query(String name, String description, String sql) {
        this(name, description, sql, null);
    }

    public Query(String name, String description, String sql, Duration cacheableDuration) {
        this.name = name;
        this.description = description;
        this.sql = sql;
        this.cacheableDuration = cacheableDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSql() {
        return sql;
    }

    public Duration getCacheableDuration() {
        return cacheableDuration;
    }


    @SuppressWarnings("unused") //used in QueryDao by name
    public Long getCacheableDurationInMs() {
        return cacheableDuration == null ? null : cacheableDuration.toMillis();
    }
}
