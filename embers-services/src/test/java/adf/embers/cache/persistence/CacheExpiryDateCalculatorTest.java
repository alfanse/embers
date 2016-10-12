package adf.embers.cache.persistence;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


public class CacheExpiryDateCalculatorTest {

    private static final int ONE_HOUR_MS = (1000 * 60 * 60);
    private final ZoneId london = ZoneId.of("Europe/London");
    private Date startingDate;

    @Before
    public void safeDateThatWontCrossDST() throws Exception {
        startingDate = createDate(2016, Month.APRIL, 30);
    }

    @Test
    //noticed odd behaviour when test randomly failed for a certain date,
    // turns out the api adds 1 hour when crosses daylight saving time
    public void addingMillisWhenBstStarts() throws Exception {
        long millisToAdd = ONE_HOUR_MS*48;
        //day before BST starts
        Date dateBeforeDst = createDate(2016, Month.MARCH, 26);

        Date calculated = new CacheExpiryDateCalculator(dateBeforeDst, millisToAdd, london).invoke();

        //48 hours later, +1 hour for DST.
        assertThat(calculated).withDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm")).isEqualTo("2016-3-28 02:02");
    }

    @Test
    //noticed odd behaviour when test randomly failed for a certain date,
    // turns out the api adds 1 hour when crosses daylight saving time
    public void addingMillisWhenBstFallsBack() throws Exception {
        long millisToAdd = ONE_HOUR_MS*48;
        //the day before British Summer Time fallback
        Date start = createDate(2016, Month.OCTOBER, 29);

        Date calculated = new CacheExpiryDateCalculator(start, millisToAdd, london).invoke();

        //48 hours later, -1 hour for DST fallback.
        assertThat(calculated).withDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm")).isEqualTo("2016-10-31 00:02");
    }

    @Test
    public void addingMillisOneMilliMoreThanMaxInteger() throws Exception {
        long millisToAdd = Integer.MAX_VALUE+1L;
        didItWork(startingDate, millisToAdd);
    }

    @Test
    public void addingOneYearOfMillis() throws Exception {
        long millisToAdd = 1000*60*60*24*365L;
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
        return Date.from(ldt.atZone(london).toInstant());
    }

    private void didItWork(Date startingDate, long millisToAdd) {
        long startingDateTime = startingDate.getTime();
        Date calculateFutureDate = new CacheExpiryDateCalculator(startingDate, millisToAdd).invoke();
        assertThat(calculateFutureDate.getTime())
                .describedAs("unexpected difference: "+(calculateFutureDate.getTime()- startingDateTime -millisToAdd))
                .isEqualTo(startingDateTime + millisToAdd);
    }


}