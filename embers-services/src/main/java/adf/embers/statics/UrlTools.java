package adf.embers.statics;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlTools {

    public static String decodeString(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode: " + encodedString, e);
        }
    }

    public static String encodeString(String plainString){
        try {
            return URLEncoder.encode(plainString, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode: " + plainString, e);
        }
    }

}
