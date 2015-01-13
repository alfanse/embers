package adf.embers.query.persistence;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(QueryMapper.class)
/** JDBI implements this for access to Queries table */
public interface QueryDao {

    String COL_ID = "id";
    String COL_NAME = "name";
    String COL_DESCRIPTION = "description";
    String COL_SQL = "sql";

    @SqlUpdate("Insert into queries (name, description, sql) values (:q.name, :q.description, :q.sql)")
    void save(@BindBean("q") Query query);

    @SqlQuery("select * from queries where name = :queryName")
    Query findQueryByName(@Bind(value = "queryName") String queryName);
}
