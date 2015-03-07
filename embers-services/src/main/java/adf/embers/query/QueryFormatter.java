package adf.embers.query;

import java.util.List;
import java.util.Map;

public interface QueryFormatter {
    String format(List<Map<String, Object>> result);
}
