package adf.embers.query.persistence.cacheing;

import com.google.gson.Gson;
import org.junit.Test;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;


public class ClobToQueryResultTest {

    public static final String KEY = "key";
    private final ClobToQueryResult clobToQueryResult = new ClobToQueryResult();

    @Test
    /** null specifically means cache never been populated */
    public void handleNullClobByReturningNull() throws Exception {
        List<Map<String, Object>> queryResult = clobToQueryResult.deserialise(null);
        assertThat(queryResult).isNull();
    }

    @Test
    public void deserialiseJsonStringsIntoQueryResult() throws Exception {

        String value = "a string, with \"punctuation\".";
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = clobToQueryResult.deserialise(clob);

        assertThat(queryResult.get(0).get(KEY)).isEqualTo(value);
    }

    @Test
    /*Json serialises the date into a string with Json format */
    public void deserialiseJsonDateIntoQueryResult() throws Exception {

        Date value = new Date();
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = clobToQueryResult.deserialise(clob);

        assertThat(queryResult.get(0).get(KEY)).isEqualTo(new SimpleDateFormat("MMM dd, yyyy h:mm:ss aa").format(value));
    }

    @Test
    /*Json serialises the date into a string with Json format */
    public void deserialiseJsonFloatIntoQueryResult() throws Exception {

        Float value = 123456789.987654321f;
        Clob clob = givenQueryResultedInASingleValueOf(value);

        List<Map<String, Object>> queryResult = clobToQueryResult.deserialise(clob);

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

}