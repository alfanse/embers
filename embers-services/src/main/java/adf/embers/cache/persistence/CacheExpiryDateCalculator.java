package adf.embers.cache.persistence;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class CacheExpiryDateCalculator {

    private static final int MILLIS_IN_HOUR = 1000*60*60;
    private static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;
    private Date startingDate;
    private long millisToAdd;
    private ZoneId zone;

    public CacheExpiryDateCalculator(Date startingDate, long millisToAdd, ZoneId zone) {
        this.startingDate = startingDate;
        this.millisToAdd = millisToAdd;
        this.zone = zone;
    }

    public CacheExpiryDateCalculator(Date startingDate, long millisToAdd) {
        this(startingDate, millisToAdd, ZoneId.systemDefault());
    }

    public Date invoke() {
        if(isCacheTimeBiggerThanIntegerMaxValueInDays()) {
            throw new RuntimeException("Cache duration greater than acceptable limit");
        }

        return calculatingFutureDateWithDateTime();
    }

    private Date calculatingFutureDateWithDateTime() {
        ZonedDateTime now = ZonedDateTime.ofInstant(startingDate.toInstant(), zone);
        ZonedDateTime future = now.plus(millisToAdd, ChronoUnit.MILLIS);
        return  Date.from(future.toInstant());
    }

    private boolean isCacheTimeBiggerThanIntegerMaxValueInDays() {
        return (millisToAdd / MILLIS_IN_DAY) > Integer.MAX_VALUE;
    }

}
