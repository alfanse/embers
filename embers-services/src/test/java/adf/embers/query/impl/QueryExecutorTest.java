package adf.embers.query.impl;

import adf.embers.query.persistence.Query;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class QueryExecutorTest {

    private final DBI dbiFactory = mock(DBI.class);
    private final Handle handle = mock(Handle.class);

    @Test
    public void useDbiToExecuteQuerySql_returnsResult_closeConnection() {
        Query query = givenAQuery();

        given(dbiFactory.open()).willReturn(handle);
        List<Map<String, Object>> expectedResult = Collections.emptyList();
        given(handle.select(query.getSql())).willReturn(expectedResult);

        List<Map<String, Object>> result = new QueryExecutor(dbiFactory).runQuery(query);

        assertThat(result).isEqualTo(expectedResult);

        verify(handle).close();
    }

    private Query givenAQuery() {
        return new Query("name", "desc", "sql");
    }
}