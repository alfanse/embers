package adf.embers.tools;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.function.Consumer;

public class AssertionMatcher<EXPECTED_TYPE> extends TypeSafeMatcher<EXPECTED_TYPE> {
    private Consumer<EXPECTED_TYPE> assertions;

    public AssertionMatcher(Consumer<EXPECTED_TYPE> assertions) {
        this.assertions = assertions;
    }

    @Override
    protected boolean matchesSafely(EXPECTED_TYPE item) {
        assertions.accept(item);
        return true;
    }

    @Override
    public void describeTo(Description description) {

    }
}
