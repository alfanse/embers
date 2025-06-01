package adf.embers.cache;

import adf.embers.query.QueryProcessor;
import adf.embers.query.QueryRequest;
import adf.embers.query.QueryResult;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class QueryResultCacheHandlerTest {

    @Mock
    QueryProcessor queryProcessor;

    @Mock
    QueryResult serviceResponse;

    @InjectMocks
    QueryResultCacheHandler queryResultCacheHandler;

    @Test
    public void whenServiceErrors_respondWith404AndErrorMessage() {
        given(serviceResponse.hasErrors()).willReturn(true);
        given(serviceResponse.getErrorMessages()).willReturn("bunch of errors");

        given(queryProcessor.placeQuery(any(QueryRequest.class))).willReturn(serviceResponse);

        Response response = queryResultCacheHandler.executeQuery("some query");

        assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
        assertThat(response.getEntity()).isEqualTo("bunch of errors");
    }
}