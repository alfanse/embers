package adf.embers.query;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public interface QueryResult {
    String getResult();

    boolean hasErrors();

    List<String> getErrors();

    Date getCachedOn();

    default String getErrorMessages() {
        return getErrors().stream().collect(Collectors.joining(System.lineSeparator()));
    }
}
