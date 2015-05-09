package adf.embers.statics;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlTools {

    public static String decodeString(String encodedString) {
        try {
            return URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to statics : " + encodedString);
        }
    }

    public static String encodeString(String plainString){
        try {
            return URLEncoder.encode(plainString, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode "+plainString, e);
        }
    }

}
