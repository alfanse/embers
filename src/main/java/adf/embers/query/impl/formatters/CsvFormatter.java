package adf.embers.query.impl.formatters;

import adf.embers.query.QueryFormatter;
import adf.embers.query.QueryRequest;

import java.util.List;
import java.util.Map;

/* Format the Dbi result List into CSV, with header row and data rows.
* Assumes the Map for each row iterates in the same order. */
public class CsvFormatter implements QueryFormatter {

    public static final String COMMA = ",";
    public static final String NEW_LINE = "\n";

    @Override
    public String format(List<Map<String, Object>> result, QueryRequest queryRequest) {

        if(result.size() < 1) {
            return "";
        }

        StringBuilder outputString = new StringBuilder();

        appendHeaderRow(result, outputString);
        appendValues(result, outputString);

        return outputString.toString();
    }

    private void appendHeaderRow(List<Map<String, Object>> result, StringBuilder outputString) {
        for (String key : result.get(0).keySet()) {
            outputString.append(key).append(COMMA);
        }
        removeTrailingComma(outputString);
        newLine(outputString);
    }

    private void appendValues(List<Map<String, Object>> result, StringBuilder outputString) {
        for (Map<String, Object> stringObjectMap : result) {
            appendRowOfValues(outputString, stringObjectMap);
        }
    }

    private void appendRowOfValues(StringBuilder outputString, Map<String, Object> stringObjectMap) {
        for (Object value : stringObjectMap.values()) {
            outputString.append(convertObjectToString(value)).append(COMMA);
        }
        removeTrailingComma(outputString);
        newLine(outputString);
    }

    //todo handle all the wierd jdbs sql types - or not, rely on sql to format dates etc nicely?
    private String convertObjectToString(Object value) {
        return value.toString();
    }

    private void removeTrailingComma(StringBuilder stringBuilder) {
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
    }

    private void newLine(StringBuilder outputString) {
        outputString.append(NEW_LINE);
    }
}
