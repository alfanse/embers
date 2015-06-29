package adf.embers.cache.persistence;

import java.util.Calendar;
import java.util.Date;

class CalculateFutureDate {

    public static final int MILLIS_IN_HOUR = 1000*60*60;
    public static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;
    private Date startingDate;
    private long millisToAdd;

    public CalculateFutureDate(Date startingDate, long millisToAdd) {
        this.startingDate = startingDate;
        this.millisToAdd = millisToAdd;
    }

    public Date invoke() {

        Calendar futureDate = new Calendar.Builder().setInstant(startingDate.getTime()).build();

        long remainingMillisToAdd = millisToAdd;
        if (remainingMillisToAdd > Integer.MAX_VALUE) {

            if(isCacheTimeBiggerThanIntegerMaxValueInDays()) {
                throw new RuntimeException("Cache duration greater than acceptable limit");
            }
            //try to reduce a massive long to days, then hours before treating as millis
            remainingMillisToAdd = addUnitOfTime(futureDate, remainingMillisToAdd, Calendar.DAY_OF_YEAR, MILLIS_IN_DAY);
            remainingMillisToAdd = addUnitOfTime(futureDate, remainingMillisToAdd, Calendar.HOUR, MILLIS_IN_HOUR);
        }

        futureDate.add(Calendar.MILLISECOND, (int) remainingMillisToAdd);

        return futureDate.getTime();
    }

    private boolean isCacheTimeBiggerThanIntegerMaxValueInDays() {
        return (millisToAdd / MILLIS_IN_DAY) > Integer.MAX_VALUE;
    }

    private long addUnitOfTime(Calendar instance, long remainingMillis, int unitOfTime, long millisInUnit) {
        if(remainingMillis > millisInUnit) {
            int unitsToAdd = (int) (remainingMillis / millisInUnit);
            instance.add(unitOfTime, unitsToAdd);

            remainingMillis = remainingMillis % millisInUnit;
        }
        return remainingMillis;
    }
}
