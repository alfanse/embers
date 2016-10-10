package adf.embers.cache.persistence;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class CacheExpiryDateCalculator {

    private static final int MILLIS_IN_HOUR = 1000*60*60;
    private static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;
    private Date startingDate;
    private long millisToAdd;

    public CacheExpiryDateCalculator(Date startingDate, long millisToAdd) {
        this.startingDate = startingDate;
        this.millisToAdd = millisToAdd;
    }

    public Date invoke() {
        if(isCacheTimeBiggerThanIntegerMaxValueInDays()) {
            throw new RuntimeException("Cache duration greater than acceptable limit");
        }

        return calculatingFutureDateWithDateTime();
    }

    private Date calculatingFutureDateWithDateTime() {
        LocalDateTime ldt = LocalDateTime.ofInstant(startingDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime plus = ldt.plus(millisToAdd, ChronoUnit.MILLIS);
        return  Date.from(plus.atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean isCacheTimeBiggerThanIntegerMaxValueInDays() {
        return (millisToAdd / MILLIS_IN_DAY) > Integer.MAX_VALUE;
    }

}
