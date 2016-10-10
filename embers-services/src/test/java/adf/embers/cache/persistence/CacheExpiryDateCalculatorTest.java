package adf.embers.cache.persistence;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


public class CacheExpiryDateCalculatorTest {

    private Date startingDate;

    @Before
    public void safeDateThatWontCrossDST() throws Exception {
        startingDate = createDate(2016, Month.APRIL, 30);
    }

    @Test
    //noticed odd behaviour when test randomly failed for a certain date,
    // turns out the api adds 1 hour when crosses daylight saving time
    public void addingMillisWhenCrossesDST() throws Exception {
        long millisToAdd = Integer.MAX_VALUE;
        Date dateNearDst = createDate(2016, Month.OCTOBER, 9);

        Date calculateFutureDate = new CacheExpiryDateCalculator(dateNearDst, millisToAdd).invoke();

        assertThat(calculateFutureDate.getTime())
                .describedAs("unexpected difference: "+(calculateFutureDate.getTime() - dateNearDst.getTime() - millisToAdd))
                .isEqualTo(dateNearDst.getTime() + millisToAdd + (1000*60*60));
    }

    @Test
    public void addingMillisOneMilliMoreThanMaxInteger() throws Exception {
        long millisToAdd = Integer.MAX_VALUE+1l;
        didItWork(startingDate, millisToAdd);
    }

    @Test
    public void addingOneYearOfMillis() throws Exception {
        long millisToAdd = 1000*60*60*24*365;
        didItWork(startingDate, millisToAdd);
    }

    @Test (expected = RuntimeException.class)
    //cache limit = max int in days
    public void blowTheCacheableLimit() throws Exception {
        long millisToAdd = Long.MAX_VALUE;
        new CacheExpiryDateCalculator(startingDate, millisToAdd).invoke();
    }

    private Date createDate(final int year, final Month month, final int dayOfMonth) {
        LocalDateTime ldt = LocalDateTime.of(year, month, dayOfMonth, 1, 2);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void didItWork(Date startingDate, long millisToAdd) {
        long startingDateTime = startingDate.getTime();
        Date calculateFutureDate = new CacheExpiryDateCalculator(startingDate, millisToAdd).invoke();
        assertThat(calculateFutureDate.getTime())
                .describedAs("unexpected difference: "+(calculateFutureDate.getTime()- startingDateTime -millisToAdd))
                .isEqualTo(startingDateTime + millisToAdd);
    }


}