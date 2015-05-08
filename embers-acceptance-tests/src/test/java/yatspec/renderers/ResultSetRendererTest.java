package yatspec.renderers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.fest.assertions.api.Assertions.assertThat;

public class ResultSetRendererTest {

    @Test
    public void renderEmptyTable() throws Exception {
        String render = whenRenderIsCalledFor(emptyList());
        assertThat(render).isEqualTo("");
    }

    @Test
    public void renderResultWithMultipleColumnsAsOneHeaderRowAndSeveralDataRows(){
        HashMap<String, Object> rowOne = new HashMap<>();
        rowOne.put("column1", "value1");
        rowOne.put("column2", "value2");
        HashMap<String, Object> rowtwo = new HashMap<>();
        rowtwo.put("column1", "value2.1");
        rowtwo.put("column2", "value2.2");
        ArrayList<Map<String, Object>> resultSet = new ArrayList<>();
        resultSet.add(rowOne);
        resultSet.add(rowtwo);
        String htmlResult = whenRenderIsCalledFor(resultSet);
        assertThat(htmlResult).startsWith("<table>").endsWith("</table>");
        assertThat(htmlResult).contains("<tr><th>column1</th><th>column2</th></tr>");
        assertThat(htmlResult).contains("<tr><td>value1</td><td>value2</td></tr>");
        assertThat(htmlResult).contains("<tr><td>value2.1</td><td>value2.2</td></tr>");
    }

    private String whenRenderIsCalledFor(List<Map<String, Object>> resultSet) {
        ResultSetRenderer resultSetRenderer = new ResultSetRenderer();
        return resultSetRenderer.render(new ResultSetWrapper(resultSet));
    }
}