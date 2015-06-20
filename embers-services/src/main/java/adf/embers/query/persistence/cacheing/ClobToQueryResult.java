package adf.embers.query.persistence.cacheing;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClobToQueryResult {

    public static final List<Map<String, Object>> TYPE_OF_RESULT = new ArrayList<>();

    public List<Map<String, Object>> deserialise(Clob clob) {
        if (clob == null) {
            return null;
        }
        return new Gson().fromJson(clobBackToJsonString(clob), TYPE_OF_RESULT.getClass());
    }

    private String clobBackToJsonString(java.sql.Clob data) {
        final StringBuilder sb = new StringBuilder();

        try {
            final Reader reader = data.getCharacterStream();
            final BufferedReader br = new BufferedReader(reader);

            int b;
            while (-1 != (b = br.read())) {
                sb.append((char) b);
            }

            br.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialise the cached query result from the database");
        }

        return sb.toString();
    }
}
