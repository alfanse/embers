package adf.embers.cache.persistence;

import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


public class CalculateFutureDateTest {

    @Test
    public void addingMillisLessThanMaxInteger() throws Exception {
        long millisToAdd = Integer.MAX_VALUE;
        didItWork(new Date(), millisToAdd);
    }

    @Test
    public void addingMillisOneMilliMoreThanMaxInteger() throws Exception {
        long millisToAdd = Integer.MAX_VALUE+1l;
        didItWork(new Date(), millisToAdd);
    }

    @Test
    public void addingOneYearOfMillis() throws Exception {
        long millisToAdd = 1000*60*60*24*365;
        didItWork(new Date(), millisToAdd);
    }

    @Test (expected = RuntimeException.class)
    public void blowTheCacheableLimit() throws Exception {
        Date now = new Date();
        long millisToAdd = Long.MAX_VALUE - now.getTime();
        new CalculateFutureDate(now, millisToAdd).invoke();
    }

    private void didItWork(Date startingDate, long millisToAdd) {
        Date futureDate = new CalculateFutureDate(startingDate, millisToAdd).invoke();
        assertThat(futureDate.getTime()).isEqualTo(startingDate.getTime() + millisToAdd);
    }


}