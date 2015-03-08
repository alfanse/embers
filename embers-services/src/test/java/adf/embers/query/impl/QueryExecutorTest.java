package adf.embers.query.impl;

import adf.embers.configuration.DbiHandleFactory;
import adf.embers.query.persistence.Query;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryExecutorTest {

    private final DbiHandleFactory dbiFactory = mock(DbiHandleFactory.class);
    private final Handle handle = mock(Handle.class);

    @Test
    public void useDbiHandlerToExecuteQuerySql_returnsResult() throws Exception {
        Query query = givenAQuery();

        when(dbiFactory.getHandle()).thenReturn(handle);
        List<Map<String, Object>> expectedResult = Collections.emptyList();
        when(handle.select(query.getSql())).thenReturn(expectedResult);

        List<Map<String, Object>> result = new QueryExecutor(dbiFactory).runQuery(query);

        assertThat(result).isEqualTo(expectedResult);
    }

    private Query givenAQuery() {
        return new Query("name", "desc", "sql");
    }
}