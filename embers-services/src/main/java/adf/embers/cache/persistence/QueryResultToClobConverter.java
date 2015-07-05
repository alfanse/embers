package adf.embers.cache.persistence;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryResultToClobConverter {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Map<String, Object>> TYPE_OF_RESULT = new ArrayList<>(0);

    public String serialise(List<Map<String, Object>> cachedQueryResult) {
        return new Gson().toJson(cachedQueryResult);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> deserialise(Clob clob) {
        if (clob == null) {
            return null;
        }
        return new Gson().fromJson(clobBackToJsonString(clob), TYPE_OF_RESULT.getClass());
    }

    private String clobBackToJsonString(java.sql.Clob data) {

        try (final BufferedReader br = new BufferedReader(data.getCharacterStream())){
            final StringBuilder sb = new StringBuilder();
            int b;
            while (-1 != (b = br.read())) {
                sb.append((char) b);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialise the cached query result from the database");
        }

    }
}