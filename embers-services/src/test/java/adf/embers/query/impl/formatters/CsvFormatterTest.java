package adf.embers.query.impl.formatters;

import adf.embers.query.QueryRequest;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CsvFormatterTest {
    private final QueryRequest queryRequest = mock(QueryRequest.class);
    private final CsvFormatter csvFormatter = new CsvFormatter();
    private long idCounter = 1;

    @Test
    public void formatEmptyResult() throws Exception {
        String format = csvFormatter.format(emptyList(), queryRequest);
        assertThat(format).isEmpty();
    }

    @Test
    public void formatSeveralRowsOfDataIntoHeaderAndDataRows() throws Exception {
        //noinspection unchecked
        List<Map<String, Object>> result = givenARowOfDataFromQueriesTable(getMapOfOneRowOfData(), getMapOfOneRowOfData());

        String format = csvFormatter.format(result, queryRequest);
        String[] rowsArray = format.split("\n");
        String headerRow = rowsArray[0];
        assertThat(headerRow).isEqualTo("string,long");
        String dataRow1 = rowsArray[1];
        assertThat(dataRow1).isEqualTo("stringValue,1");
        String dataRow2 = rowsArray[2];
        assertThat(dataRow2).isEqualTo("stringValue,2");
    }

    @SafeVarargs
    private final List<Map<String, Object>> givenARowOfDataFromQueriesTable(final Map<String, Object>... oneRowOfDataAsMap) {
        return asList(oneRowOfDataAsMap);
    }

    private Map<String, Object> getMapOfOneRowOfData() {
        Map<String, Object> aRowOFDataFromQueriesTable = new LinkedHashMap<>();
        aRowOFDataFromQueriesTable.put("string", "stringValue");
        aRowOFDataFromQueriesTable.put("long", idCounter++);
        return aRowOFDataFromQueriesTable;
    }
}