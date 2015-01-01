package adf.embers.integration.persistence;

import adf.embers.EmbersDatabase;
import adf.embers.query.persistence.QueriesDao;
import adf.embers.query.persistence.Query;
import org.fest.assertions.core.Condition;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class QueriesDaoIntegrationTest {

    private EmbersDatabase embersDatabase;

    @Before
    public void setupDB() throws Exception {
        embersDatabase = new EmbersDatabase("jdbc:hsqldb:mem:daoTest");
        embersDatabase.startInmemoryDatabase();
        embersDatabase.createTableQueries();
    }

    @Test
    public void saveAndFindQueries() {
        DBI dbi = new DBI(embersDatabase.getDataSource());
        QueriesDao queriesDao = dbi.open(QueriesDao.class);

        Query queryToSave = new Query("allQueries", "Shows all the available queries", "select id, name, description, sql from queries order by name");
        queriesDao.save(queryToSave);

        List<Query> actualResponse = queriesDao.findAll();
        assertThat(actualResponse).hasSize(1);
        assertThat(actualResponse.get(0)).has(new Condition<Query>() {
            @Override
            public boolean matches(Query value) {
                assertThat(value.getId()).isGreaterThan(0);
                assertThat(value.getName()).isEqualTo(queryToSave.getName());
                assertThat(value.getDescription()).isEqualTo(queryToSave.getDescription());
                assertThat(value.getSql()).isEqualTo(queryToSave.getSql());
                return true;
            }
        });
    }
}