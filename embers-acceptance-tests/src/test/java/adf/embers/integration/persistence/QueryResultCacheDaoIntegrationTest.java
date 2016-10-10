package adf.embers.integration.persistence;

import adf.embers.cache.persistence.CachedQuery;
import adf.embers.cache.persistence.QueryResultCacheDao;
import adf.embers.query.QueryRequest;
import adf.embers.tools.EmbersDatabase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class QueryResultCacheDaoIntegrationTest {

    private static EmbersDatabase embersDatabase;
    private QueryResultCacheDao queryResultCacheDao;

    @BeforeClass
    public static void setupDB() throws Exception {
        embersDatabase = new EmbersDatabase("jdbc:hsqldb:mem:daoTest");
        embersDatabase.startInMemoryDatabase();
        embersDatabase.createTableQueryResultCache();
    }

    @Before
    public void setupDao() {
        DBI dbi = new DBI(embersDatabase.getDataSource());
        queryResultCacheDao = dbi.open(QueryResultCacheDao.class);

        embersDatabase.clearTableQueryResultCache();
    }

    @Test
    public void canSaveThenFindThenSaveCachedResult() throws Exception {
        QueryRequest query = () -> "queryName";
        Duration liveDuration = Duration.ofHours(1);

        CachedQuery cachedQuery = new CachedQuery(query.getQueryName(), liveDuration.toMillis());
        assertThat(cachedQuery.getQueryName()).isEqualTo(query.getQueryName());
        assertThat(cachedQuery.getTimestampWhenCached()).isNull();
        assertThat(cachedQuery.getResult()).isNull();
        assertThat(cachedQuery.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
        assertThat(cachedQuery.isCacheMiss()).isTrue();

        queryResultCacheDao.save(cachedQuery);

        CachedQuery findResult = queryResultCacheDao.findCachedQueryResult(query);
        assertThat(findResult.getQueryName()).isEqualTo(query.getQueryName());
        assertThat(findResult.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
        assertThat(findResult.isCacheMiss()).isTrue();
        assertThat(findResult.getResult()).isNull();
        assertThat(findResult.getTimestampWhenCached()).isNull();

        ArrayList<Map<String, Object>> resultToCache = new ArrayList<>();
        Map<String, Object> aRowOfData = new HashMap<>();
        aRowOfData.put("string", "a string, with \"punctuation\".");
        resultToCache.add(aRowOfData);
        cachedQuery.setResult(resultToCache);

        Date dateResultRetrieved = new Date();
        cachedQuery.setDateWhenCached(dateResultRetrieved);

        queryResultCacheDao.updateQueryCacheResult(cachedQuery);

        CachedQuery findCachedResult = queryResultCacheDao.findCachedQueryResult(query);
        assertThat(findCachedResult.getQueryName()).isEqualTo(query.getQueryName());
        assertThat(findCachedResult.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
        assertThat(findCachedResult.isCacheMiss()).isFalse();
        assertThat(findCachedResult.getTimestampWhenCached()).isEqualTo(dateResultRetrieved);
        assertThat(findCachedResult.getResult()).isEqualTo(resultToCache);
    }

}