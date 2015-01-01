package adf.embers.query.persistence;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(QueriesMapper.class)
public interface QueriesDao {

    String TABLE_QUERIES = "queries";

    String COL_ID = "id";
    String COL_NAME = "name";
    String COL_DESCRIPTION = "description";
    String COL_SQL = "sql";

    @SqlQuery("select * from " + TABLE_QUERIES + " order by " + COL_NAME)
    List<Query> findAll();

    @SqlUpdate("Insert into queries (name, description, sql) values (:q.name, :q.description, :q.sql)")
    void save(@BindBean("q") Query query);
}
