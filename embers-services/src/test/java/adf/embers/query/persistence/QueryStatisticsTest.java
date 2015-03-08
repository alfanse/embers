package adf.embers.query.persistence;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class QueryStatisticsTest {

    @Test
    public void calculatesDuration() throws Exception {
        final QueryStatistics queryStatistics = new QueryStatistics(mock(Query.class));
        Thread.sleep(1);
        queryStatistics.setDuration();
        assertThat(queryStatistics.getDuration()).isGreaterThan(0);
    }
}