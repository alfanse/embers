package adf.embers.tools;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import yatspec.renderers.ResultSetWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ResultSetWrapperMatcher extends TypeSafeMatcher<ResultSetWrapper> {

    private Consumer<List<Map<String, Object>>> assertionFunction;

    public ResultSetWrapperMatcher(Consumer<List<Map<String, Object>>> assertionFunction) {
        this.assertionFunction = assertionFunction;
    }

    @Override
    protected boolean matchesSafely(ResultSetWrapper item) {
        assertionFunction.accept(item.getResultSet());
        return true;
    }

    @Override
    public void describeTo(Description description) {

    }
}
