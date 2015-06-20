package adf.embers.integration.persistence;

import adf.embers.query.persistence.CachedQuery;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryResultCacheDao;
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
        Query query = new Query("queryName", "query description", "select timestamp from "+QueryResultCacheDao.TABLE_QUERIES_RESULT_CACHE);
        Duration liveDuration = Duration.ofHours(1);

        CachedQuery cachedQuery = new CachedQuery(query, liveDuration);
        assertThat(cachedQuery.getQueryName()).isEqualTo(query.getName());
        assertThat(cachedQuery.getDateCached()).isNull();
        assertThat(cachedQuery.getCachedQueryResult()).isNull();
        assertThat(cachedQuery.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
        assertThat(cachedQuery.isCacheMiss()).isTrue();

        queryResultCacheDao.save(cachedQuery);

        CachedQuery findResult = queryResultCacheDao.findCachedQueryResult(query);
        assertThat(findResult.getQueryName()).isEqualTo(query.getName());
        assertThat(findResult.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
        assertThat(findResult.isCacheMiss()).isTrue();
        assertThat(findResult.getCachedQueryResult()).isNull();
        assertThat(findResult.getDateCached()).isNull();

        Date dateResultRetrieved = new Date();
        ArrayList<Map<String, Object>> resultToCache = new ArrayList<>();
        Map<String, Object> aRowOfData = new HashMap<>();
        aRowOfData.put("string", "a string, with \"punctuation\".");
        aRowOfData.put("integer", 123456789);
        aRowOfData.put("floats", 987654321.123456789f);
        aRowOfData.put("date", new Date());
        resultToCache.add(aRowOfData);
        cachedQuery.setCachedQueryResult(resultToCache);
        cachedQuery.setDateCached(dateResultRetrieved);

        //TODO implement cacheing
//        queryResultCacheDao.updateQueryCacheResult(cachedQuery);

//        CachedQuery findCachedResult = queryResultCacheDao.findCachedQueryResult(query);
//        assertThat(findCachedResult.getQueryName()).isEqualTo(query.getName());
//        assertThat(findCachedResult.getLiveDurationMs()).isEqualTo(liveDuration.toMillis());
//        assertThat(findCachedResult.isCacheMiss()).isFalse();
//        assertThat(findCachedResult.getDateCached()).isAfterOrEqualsTo(dateResultRetrieved);
//        assertThat(findCachedResult.getCachedQueryResult()).isEqualTo(resultToCache);
    }

}