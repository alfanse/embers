# Flickering test

adf.embers.cache.persistence.CachedQueryTest

Test isCacheHitWhenJustBeenCached() PASSED
Test isCacheMissWhenOneMsMoreThanCachebleDuration() PASSED
Test isCacheHitWhenOnCachebleDuration() FAILED

java.lang.AssertionError: expected:
<false>
but was:
<true>
at adf.embers.cache.persistence.CachedQueryTest.isCacheHitWhenOnCachebleDuration(CachedQueryTest.java:35)
