package adf.embers.statics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UrlToolsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void decodeErrorsThrownAsRuntimeException() {
        String encodedString = "%x";
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Failed to decode: "+encodedString);

        UrlTools.decodeString(encodedString);
    }
}