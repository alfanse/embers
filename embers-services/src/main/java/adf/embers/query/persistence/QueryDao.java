package adf.embers.query.persistence;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(QueryMapper.class)
/**
 * DAO access to A table holding details of the queries that the Embers QueryHandler can run.
 * JDBI implements this for access to the table called: QUERIES.
 */
public interface QueryDao {

    String TABLE_QUERIES = "queries";
    String COL_ID = "id";
    String COL_NAME = "name";
    String COL_DESCRIPTION = "description";
    String COL_SQL = "sql";
    String COL_CACHEABLE_DURATION = "cacheable_duration";
    String METHOD_NAME_GET_CACHEABLE_DURATION_IN_MS = "cacheableDurationInMs";

    @SqlQuery("select * from " + TABLE_QUERIES + " where " + COL_NAME + " = :queryName")
    Query findQueryByName(@Bind(value = "queryName") String queryName);

    @SqlUpdate("Insert into " + TABLE_QUERIES + " ("
            + COL_NAME + ", " + COL_DESCRIPTION + ", " + COL_SQL + "," +COL_CACHEABLE_DURATION
            + ") values (:q.name, :q.description, :q.sql, :q." + METHOD_NAME_GET_CACHEABLE_DURATION_IN_MS + ")")
    void save(@BindBean("q") Query query);

    @SqlUpdate("Update "+TABLE_QUERIES
            +" set " +COL_DESCRIPTION+"=:q.description, "
                     +COL_SQL+"=:q.sql, "
                     +COL_CACHEABLE_DURATION+ "=:q." + METHOD_NAME_GET_CACHEABLE_DURATION_IN_MS + " " +
            " where "+COL_NAME+"=:q.name")
    void update(@BindBean("q") Query query);

    @SqlUpdate("Delete from "+TABLE_QUERIES+" where "+COL_NAME+"=:queryName")
    void delete(@Bind(value = "queryName") String queryName);
}
