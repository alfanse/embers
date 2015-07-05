package adf.embers.cache.persistence;

import com.google.gson.Gson;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

import static java.time.LocalDateTime.of;
import static org.fest.assertions.api.Assertions.assertThat;


public class QueryResultToClobConverterTest {

    public static final String KEY = "key";
    private final QueryResultToClobConverter queryResultToClobConverter = new QueryResultToClobConverter();

    @Test
    /** null specifically means cache never been populated */
    public void deserialiseNullClobByReturningNull() throws Exception {
        List<Map<String, Object>> queryResult = queryResultToClobConverter.deserialise(null);
        assertThat(queryResult).isNull();
    }

    @Test
    public void deserialiseJsonStringsIntoQueryResult() throws Exception {

        String value = "a string, with \"punctuation\".";
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = queryResultToClobConverter.deserialise(clob);

        assertThat(queryResult.get(0).get(KEY)).isEqualTo(value);
    }

    @Test
    /*Json serialises the date into a string with Json format */
    public void deserialiseJsonDateWithIntoQueryResult() throws Exception {

        Date value = new Date();
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = queryResultToClobConverter.deserialise(clob);

        String jsonDateFormat = "MMM d, yyyy h:mm:ss aa";
        assertThat(queryResult.get(0).get(KEY)).isEqualTo(new SimpleDateFormat(jsonDateFormat).format(value));
    }

    @Test
    public void deserialiseJsonFloatIntoQueryResult() throws Exception {

        Float value = 123456789.987654321f;
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = queryResultToClobConverter.deserialise(clob);

        assertThat(queryResult.get(0).get(KEY).toString()).isEqualTo(value.toString());
    }

    private Clob givenQueryResultedInASingleValueOf(Object value) throws SQLException {
        ArrayList<Map<String, Object>> resultToCache = givenAQueryResultOfSingleKeyValue(value);

        String jsonString = givenAJsonStringOf(resultToCache);

        return new SerialClob(jsonString.toCharArray());
    }

    private ArrayList<Map<String, Object>> givenAQueryResultOfSingleKeyValue(Object value) {
        ArrayList<Map<String, Object>> resultToCache = new ArrayList<>();
        Map<String, Object> aRowOfData = new HashMap<>();
        aRowOfData.put(KEY, value);
        resultToCache.add(aRowOfData);
        return resultToCache;
    }

    private String givenAJsonStringOf(ArrayList<Map<String, Object>> resultToCache) {
        return new Gson().toJson(resultToCache);
    }

    @Test
    public void serialiseEmptyList() throws Exception {
        String jsonString = queryResultToClobConverter.serialise(Collections.emptyList());
        assertThat(jsonString).isEqualTo("[]");
    }

    @Test
    public void seraliseMultipleRowsMultipleColumnsMultiTypesIntoJson() throws Exception {
        ArrayList<Map<String, Object>> queryResult = new ArrayList<>();
        Map<String, Object> aRowOfData = new HashMap<>();
        aRowOfData.put("string", "a string, with \"punctuation\".");
        aRowOfData.put("integer", 123456789);
        aRowOfData.put("floats", 987654321.123456789f);
        aRowOfData.put("date", convertToDate(of(2015, Month.OCTOBER, 31, 23, 59, 59)));
        queryResult.add(aRowOfData);

        Map<String, Object> anotherRowOfData = new HashMap<>();
        anotherRowOfData.put("date", convertToDate(of(2015, Month.JANUARY, 5, 1, 2, 3)));
        anotherRowOfData.put("floats", 987.123f);
        anotherRowOfData.put("integer", 123);
        anotherRowOfData.put("string", "the quick brown fox.");
        queryResult.add(anotherRowOfData);

        String actual = queryResultToClobConverter.serialise(queryResult);
        String expected =
                "[{\"date\":\"Oct 31, 2015 11:59:59 PM\",\"floats\":9.8765434E8,\"string\":\"a string, with \\\"punctuation\\\".\",\"integer\":123456789}" +
                ",{\"date\":\"Jan 5, 2015 1:02:03 AM\",\"floats\":987.123,\"string\":\"the quick brown fox.\",\"integer\":123}]";
        JSONAssert.assertEquals(expected, actual, false);
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}