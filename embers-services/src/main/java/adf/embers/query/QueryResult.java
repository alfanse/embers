package adf.embers.query;

import java.util.Date;
import java.util.List;

public interface QueryResult {
    String getResult();

    boolean hasErrors();

    List<String> getErrors();

    Date getCachedOn();
}
