package yatspec.renderers;

import com.googlecode.yatspec.rendering.Renderer;

import java.util.Map;

/**
 * Yatspec renderer for List<Map<String, Object>>
 *     to render as html table with header row and data rows.
 */
public class ResultSetRenderer implements Renderer<ResultSetWrapper> {

    public String render(ResultSetWrapper resultSetWrapper) {
        if(resultSetWrapper.getResultSet().isEmpty()){
            return "";
        }

        StringBuilder html = new StringBuilder("<table>");

        appendHeaderRow(html, resultSetWrapper);

        appendDataRows(resultSetWrapper, html);

        html.append("</table>");

        return html.toString();
    }

    private void appendDataRows(ResultSetWrapper resultSetWrapper, StringBuilder html) {
        for (Map<String, Object> row : resultSetWrapper.getResultSet()) {
            appendDataRow(html, row);
        }
    }

    private void appendDataRow(StringBuilder html, Map<String, Object> row) {
        html.append("<tr>");
        for (String header : row.keySet()) {
            appendDataColumn(html, row.getOrDefault(header, ""));
        }
        html.append("</tr>");
    }

    private void appendDataColumn(StringBuilder html, Object value) {
        html.append("<td>").append(value).append("</td>");
    }

    private void appendHeaderRow(StringBuilder html, ResultSetWrapper resultSetWrapper) {
        Map<String, Object> firstRow = resultSetWrapper.getResultSet().get(0);
        html.append("<tr>");
        for (String header : firstRow.keySet()) {
            appendHeaderColumn(html, header);
        }
        html.append("</tr>");
    }

    private void appendHeaderColumn(StringBuilder html, String header) {
        html.append("<th>").append(header).append("</th>");
    }

}
