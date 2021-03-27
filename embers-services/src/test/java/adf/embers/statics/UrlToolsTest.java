package adf.embers.statics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class UrlToolsTest {


    @Test
    public void decodeErrorsThrownAsRuntimeException() {
        String encodedString = "%x";
        RuntimeException actualException = Assertions.assertThrows(RuntimeException.class, () -> UrlTools.decodeString(encodedString));
        assertThat(actualException.getMessage()).isEqualTo("Failed to decode: "+encodedString);
    }
}