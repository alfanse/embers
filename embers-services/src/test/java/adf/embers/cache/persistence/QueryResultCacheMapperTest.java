package adf.embers.cache.persistence;

import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

import javax.sql.rowset.serial.SerialClob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class QueryResultCacheMapperTest {

    private final ResultSet resultSet = Mockito.mock(ResultSet.class);
    private final StatementContext statementContext = Mockito.mock(StatementContext.class);
    private final QueryResultToClobConverter queryResultToClobConverter = Mockito.mock(QueryResultToClobConverter.class);
    private final QueryResultCacheMapper queryResultCacheMapper = new QueryResultCacheMapper(queryResultToClobConverter);

    @Test
    public void deserialiseJsonResultToListOfMaps() throws Exception {

        SerialClob clob = new SerialClob("thing".toCharArray());
        ArrayList<Map<String, Object>> resultToCache = new ArrayList<>();
        Mockito.when(queryResultToClobConverter.deserialise(clob)).thenReturn(resultToCache);
        Mockito.when(resultSet.getClob(QueryResultCacheDao.COL_RESULT)).thenReturn(clob);

        CachedQuery cachedQuery = queryResultCacheMapper.map(0, resultSet, statementContext);

        assertThat(cachedQuery.getResult()).isEqualTo(resultToCache);
    }


}