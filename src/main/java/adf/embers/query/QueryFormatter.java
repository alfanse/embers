package adf.embers.query;

import java.util.Map;

public interface QueryFormatter {
    String format(Map<String, Object> result, QueryRequest queryRequest);
}
