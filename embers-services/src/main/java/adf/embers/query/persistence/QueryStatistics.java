package adf.embers.query.persistence;

import java.sql.Timestamp;
import java.util.Date;

public class QueryStatistics {
    private final String name;
    private final Timestamp dateExecuted;
    private Long duration;
    private String result;

    public QueryStatistics(Query expectedQuery) {
        this.name = expectedQuery.getName();
        this.dateExecuted = new Timestamp(new Date().getTime());
        this.duration = 0L;
    }

    public String getName() {
        return name;
    }

    public Timestamp getDateExecuted() {
        return dateExecuted;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration() {
        this.duration = System.currentTimeMillis() - dateExecuted.getTime();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
