package adf.embers.query.persistence;

public class Query {
    Long id;
    String name, description, sql;
    //todo add audit information, created details, updated details.

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
