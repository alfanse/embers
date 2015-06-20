package adf.embers.query.persistence.cacheing;

import org.junit.Test;
import org.skife.jdbi.v2.StatementContext;

import javax.sql.rowset.serial.SerialClob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryResultCacheMapperTest {

    private final ResultSet resultSet = mock(ResultSet.class);
    private final StatementContext statementContext = mock(StatementContext.class);
    private final ClobToQueryResult clobToQueryResult = mock(ClobToQueryResult.class);
    private final QueryResultCacheMapper queryResultCacheMapper = new QueryResultCacheMapper(clobToQueryResult);

    @Test
    public void deserialiseJsonResultToListOfMaps() throws Exception {

        SerialClob clob = new SerialClob("thing".toCharArray());
        ArrayList<Map<String, Object>> resultToCache = new ArrayList<>();
        when(clobToQueryResult.deserialise(clob)).thenReturn(resultToCache);
        when(resultSet.getClob(QueryResultCacheDao.COL_RESULT)).thenReturn(clob);

        CachedQuery cachedQuery = queryResultCacheMapper.map(0, resultSet, statementContext);

        assertThat(cachedQuery.getCachedQueryResult()).isEqualTo(resultToCache);
    }


}