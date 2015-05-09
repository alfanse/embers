package adf.embers.integration.persistence;

import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryDao;
import adf.embers.tools.EmbersDatabase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import static org.fest.assertions.api.Assertions.assertThat;

public class QueryDaoIntegrationTest {

    private static EmbersDatabase embersDatabase;
    private QueryDao queryDao;

    @BeforeClass
    public static void setupDB() throws Exception {
        embersDatabase = new EmbersDatabase("jdbc:hsqldb:mem:daoTest");
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueries();
    }

    @Before
    public void setupDao() {
        DBI dbi = new DBI(embersDatabase.getDataSource());
        queryDao = dbi.open(QueryDao.class);

        embersDatabase.clearQueries();
    }

    @Test
    public void saveAndFindQueryByName() throws Exception {
        Query expectedQuery = new Query("testq", "some description", "select 1 from dual");
        queryDao.save(expectedQuery);

        final Query actualQuery = queryDao.findQueryByName(expectedQuery.getName());
        assertThat(actualQuery.getName()).isEqualTo(expectedQuery.getName());
        assertThat(actualQuery.getDescription()).isEqualTo(expectedQuery.getDescription());
        assertThat(actualQuery.getSql()).isEqualTo(expectedQuery.getSql());
        assertThat(actualQuery.getId()).isGreaterThan(0);
    }

    @Test
    public void saveAndUpdateQuery() throws Exception {
        Query expectedQuery = new Query("testq", "some description", "select 1 from dual");
        queryDao.save(expectedQuery);

        Query updatedQuery = new Query(expectedQuery.getName(), "changed description", "changed sql");
        queryDao.update(updatedQuery);

        final Query actualQuery = queryDao.findQueryByName(expectedQuery.getName());
        assertThat(actualQuery.getName()).isEqualTo(updatedQuery.getName());
        assertThat(actualQuery.getDescription()).isEqualTo(updatedQuery.getDescription());
        assertThat(actualQuery.getSql()).isEqualTo(updatedQuery.getSql());
        assertThat(actualQuery.getId()).isGreaterThan(0);
    }

    @Test
    public void findUnknown() throws Exception {
        final Query queryByName = queryDao.findQueryByName("unknown");
        assertThat(queryByName).isNull();
    }

    @Test
    public void deleteQuery(){
        final Query expectedQuery = new Query("testq", "some description", "select 1 from dual");
        queryDao.save(expectedQuery);

        queryDao.delete(expectedQuery.getName());

        final Query queryByName = queryDao.findQueryByName(expectedQuery.getName());
        assertThat(queryByName).isNull();
    }
}