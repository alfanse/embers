package adf.embers.cache;

import adf.embers.query.QueryProcessor;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class QueryResultCacheHandlerTest {

    @Mock
    QueryProcessor queryProcessor;

    @Mock
    QueryResult serviceResponse;

    @InjectMocks
    QueryResultCacheHandler queryResultCacheHandler;

    @Test
    public void whenServiceErrors_respondWithListofErrorsAnd404() {
        BDDMockito.given(serviceResponse.hasErrors()).willReturn(true);
        BDDMockito.given(serviceResponse.getErrors()).willReturn(Arrays.asList("first error", "second error", "third error"));

        BDDMockito.given(queryProcessor.placeQuery(any(QueryRequest.class))).willReturn(serviceResponse);

        Response response = queryResultCacheHandler.executeQuery("some query");

        assertThat(response.getStatus()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
        assertThat(response.getEntity()).isEqualTo("first error\nsecond error\nthird error");
    }
}