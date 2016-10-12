package adf.embers.cache.persistence;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;


public class CacheExpiryDateCalculatorTest {

    private static final int ONE_HOUR_MS = (1000 * 60 * 60);
    private final ZoneId london = ZoneId.of("Europe/London");
    private Date startingDate;

    @Before
    public void safeDateThatWontCrossDST() throws Exception {
        startingDate = createLocalDate(2016, Month.APRIL, 30);
    }

    @Test
    public void addingMillisWhenBstStarts() throws Exception {
        long millisToAdd = ONE_HOUR_MS*48;
        //day before BST starts
        Date dateBeforeDst = createLocalDate("2016-03-26 01:02", london);

        Date calculated = new CacheExpiryDateCalculator(dateBeforeDst, millisToAdd, london).invoke();

        //48 hours later, +1 hour for DST.
        assertThat(calculated).withDateFormat(setupSDF(london)).isEqualTo("2016-3-28 02:02");
    }

    @Test
    public void addingMillisWhenBstFallsBack() throws Exception {
        long millisToAdd = ONE_HOUR_MS*48;
        //the day before British Summer Time fallback
        Date start = createLocalDate(2016, Month.OCTOBER, 29);

        Date calculated = new CacheExpiryDateCalculator(start, millisToAdd, london).invoke();

        //48 hours later, -1 hour for DST fallback.
        assertThat(calculated).withDateFormat(setupSDF(london)).isEqualTo("2016-10-31 00:02");
    }

    @Test
    //noticed odd behaviour when test randomly failed for a certain date,
    // turns out the api adds 1 hour when crosses daylight saving time
    //Sunday, 13 March 2016, 02:00:00 clocks were turned forward 1 hour to
//    Sunday, 13 March 2016, 03:00:00 local daylight time instead
    public void addingMillisWhenNewYorkBstStarts() throws Exception {
        long millisToAdd = ONE_HOUR_MS*5;
        //day before BST starts
        ZoneId newYork = ZoneId.of("America/New_York");
        Date from = createLocalDate("2016-03-13 01:02", newYork);

        Date calculated = new CacheExpiryDateCalculator(from, millisToAdd, newYork).invoke();

        //48 hours later, +1 hour for DST.
        assertThat(calculated).withDateFormat(setupSDF(newYork)).isEqualTo("2016-3-13 07:02");
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

    private SimpleDateFormat setupSDF(ZoneId zoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone(zoneId));
        return sdf;
    }

    private Date createLocalDate(final String date, final ZoneId zoneId) throws ParseException {
        SimpleDateFormat simpleDateFormat = setupSDF(zoneId);
        return simpleDateFormat.parse(date);
    }

    private Date createLocalDate(final int year, final Month month, final int dayOfMonth) {
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