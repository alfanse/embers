package adf.embers.query.impl;

import adf.embers.query.QueryExecutor;
import adf.embers.query.persistence.Query;
import adf.embers.query.persistence.QueryStatistics;
import adf.embers.query.persistence.QueryStatisticsDao;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class QueryExecutorRecordingStatisticsTest {

    public static final long TRANSACTION_TIME = 1L;
    private final QueryExecutor wrappedQueryExecutor = mock(QueryExecutor.class);
    private final Query query = mock(Query.class);
    private final QueryStatisticsDao auditQueryDao = mock(QueryStatisticsDao.class);
    private final QueryExecutorRecordingStatistics queryExecutorRecordingStatistics = new QueryExecutorRecordingStatistics(wrappedQueryExecutor, auditQueryDao);

    @Test
    public void passesQueryToDelegateReturnsResult() throws Exception {
        final List<Map<String, Object>> expectedResult = givenTheDelegateReturnsAResultAfter1Millisecond();

        final List<Map<String, Object>> result = whenTheAuditingQueryExecutorIsCalled();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void auditsCall() throws Exception {
        final ArgumentCaptor<QueryStatistics> auditQueryArgumentCaptor = ArgumentCaptor.forClass(QueryStatistics.class);

        givenTheDelegateReturnsAResultAfter1Millisecond();

        final List<Map<String, Object>> result = whenTheAuditingQueryExecutorIsCalled();

        verify(auditQueryDao).save(auditQueryArgumentCaptor.capture());
        assertThat(auditQueryArgumentCaptor.getValue().getResult()).isEqualTo("Number of rows returned: " + result.size());
        assertThat(auditQueryArgumentCaptor.getValue().getDuration()).isGreaterThanOrEqualTo(TRANSACTION_TIME);
    }

    private List<Map<String, Object>> whenTheAuditingQueryExecutorIsCalled() {
        return queryExecutorRecordingStatistics.runQuery(query);
    }

    private List<Map<String, Object>> givenTheDelegateReturnsAResultAfter1Millisecond() throws InterruptedException {
        final List<Map<String, Object>> expectedResult = Collections.<Map<String, Object>>emptyList();
        when(wrappedQueryExecutor.runQuery(query)).thenAnswer(invocation -> {
            Thread.sleep(TRANSACTION_TIME);
            return expectedResult;
        });

        return expectedResult;
    }
}