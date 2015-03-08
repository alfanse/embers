package adf.embers.query.persistence;

/** Entity for the Queries Table */
public class Query {
    private Long id;
    private final String name, description, sql;

    public Query(String name, String description, String sql) {
        this.name = name;
        this.description = description;
        this.sql = sql;
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
}
