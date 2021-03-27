package adf.embers.cache.persistence;

import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CachedQueryTest {

    private static final long CACHE_DURATION_MS = 1000l;
    private final CachedQuery cachedQuery = new CachedQuery("name", CACHE_DURATION_MS);

    @Test
    public void isCacheMissWhenNoResultSetLoaded() throws Exception {
        Assertions.assertThat(cachedQuery.isCacheMiss()).isTrue();
    }

    @Test
    public void isCacheMissWhenOneMsMoreThanCachebleDuration() throws Exception {
        cachedQuery.setResult(Collections.emptyList());
        Date expiredCacheDate = getDate(Calendar.MILLISECOND, -(CACHE_DURATION_MS +1));
        cachedQuery.setDateWhenCached(expiredCacheDate);

        Assertions.assertThat(cachedQuery.isCacheMiss()).isTrue();
    }

    @Test
    public void isCacheHitWhenOnCachebleDuration() throws Exception {
        cachedQuery.setResult(Collections.emptyList());
        Date expiredCacheDate = getDate(Calendar.MILLISECOND, -CACHE_DURATION_MS);
        cachedQuery.setDateWhenCached(expiredCacheDate);

        Assertions.assertThat(cachedQuery.isCacheMiss()).isFalse();
    }

    @Test
    public void isCacheHitWhenWithinCachebleDuration() throws Exception {
        cachedQuery.setResult(Collections.emptyList());
        Date expiredCacheDate = getDate(Calendar.MILLISECOND, -(CACHE_DURATION_MS/2));
        cachedQuery.setDateWhenCached(expiredCacheDate);

        Assertions.assertThat(cachedQuery.isCacheMiss()).isFalse();
    }

    @Test
    public void isCacheHitWhenJustBeenCached() throws Exception {
        cachedQuery.setResult(Collections.emptyList());
        cachedQuery.setDateWhenCached(new Date());

        Assertions.assertThat(cachedQuery.isCacheMiss()).isFalse();
    }

    private Date getDate(int unitToModify, long amount){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(unitToModify, (int) amount);
        return cal.getTime();
    }
}