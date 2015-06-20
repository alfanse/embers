package adf.embers.query.persistence;

import adf.embers.query.persistence.cacheing.CachedQuery;
import org.junit.Test;

import java.time.Duration;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

public class CachedQueryTest {

    private final CachedQuery cachedQuery = new CachedQuery("query", Duration.ofHours(1).toMillis());

    @Test
    public void emptyMapIntoJson() throws Exception {
        cachedQuery.setCachedQueryResult(Collections.emptyList());

        String jsonString = cachedQuery.getCachedQueryResultAsJsonString();
        assertThat(jsonString).isEqualTo("[]");
    }

    @Test
    public void multipleRowsMultipleColumnsIntoJson() throws Exception {
        ArrayList<Map<String, Object>> queryResult = new ArrayList<>();
        Map<String, Object> aRowOfData = new HashMap<>();
        aRowOfData.put("string", "a string, with \"punctuation\".");
        aRowOfData.put("integer", 123456789);
        aRowOfData.put("floats", 987654321.123456789f);
        aRowOfData.put("date", new Date(1434801713301l));
        queryResult.add(aRowOfData);

        Map<String, Object> anotherRowOfData = new HashMap<>();
        anotherRowOfData.put("string", "a string, with \"punctuation\".");
        anotherRowOfData.put("integer", 123456789);
        anotherRowOfData.put("floats", 987654321.123456789f);
        anotherRowOfData.put("date", new Date(1434801713301l));
        queryResult.add(anotherRowOfData);

        cachedQuery.setCachedQueryResult(queryResult);

        String jsonString = cachedQuery.getCachedQueryResultAsJsonString();
        assertThat(jsonString).isEqualTo(
                "[{\"date\":\"Jun 20, 2015 1:01:53 PM\",\"floats\":9.8765434E8,\"string\":\"a string, with \\\"punctuation\\\".\",\"integer\":123456789}" +
                ",{\"date\":\"Jun 20, 2015 1:01:53 PM\",\"floats\":9.8765434E8,\"string\":\"a string, with \\\"punctuation\\\".\",\"integer\":123456789}]");
    }
}