package news.intelli.intellinews.utils;

import timber.log.Timber;

/**
 * Created by llefoulon on 13/11/2016.
 */

public final class Converter {

    private static final String TAG = Converter.class.getName();

    private Converter() {}

    public static int string2Int(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            Timber.w(e.toString());
            return Integer.MIN_VALUE;
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            Timber.w(e.toString());
            return false;
        }
    }
}
