package news.intelli.intellinews.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import timber.log.Timber;

/**
 * Created by llefoulon on 18/12/2016.
 */

public class StringUtils {

    private StringUtils(){}

    public static String encodeString(String string,String encode) {
        try {
            return URLEncoder.encode(string,encode);
        } catch (Exception e) {
            Timber.e(e.toString());
            return null;
        }
    }

    public static String decodeString(String string,String encode) {
        try {
            return URLDecoder.decode(string,encode);
        } catch (Exception e) {
            Timber.e(e.toString());
            return null;
        }
    }
}
