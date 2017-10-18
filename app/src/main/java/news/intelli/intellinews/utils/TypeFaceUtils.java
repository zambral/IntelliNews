package news.intelli.intellinews.utils;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;

import news.intelli.intellinews.IntelliNewsApplication;
import timber.log.Timber;


public final class TypeFaceUtils {

    private static final String TAG = TypeFaceUtils.class.getName();

    private static LruCache<String, Typeface> typefaceCache = new LruCache<>(4 * 1024 * 1024);

    public static Typeface getTypeface(String fontName) {
        if (fontName == null)
            return Typeface.DEFAULT;

        Typeface typeface;
        if ((typeface = typefaceCache.get(fontName)) == null) {
            Context context = IntelliNewsApplication.getInstanceContext();
            if (context == null)
                return Typeface.DEFAULT;

            typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            if (typeface != null)
                typefaceCache.put(fontName, typeface);
            else
                typeface = Typeface.DEFAULT;
        }

        return typeface;

    }


    public static String getXMLAttributeFontString(@NonNull Context context, AttributeSet attrs, int defStyle, int[] styleableComponent, int styleableTypeFace) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, styleableComponent, defStyle, 0);
        String specificFontName = a.getString(styleableTypeFace);
        a.recycle();

        return specificFontName;
    }

    public static String getDefaultFontString(@NonNull Context context, AttributeSet attrs, int defStyle, int[] attrsToFetch, int[] styleableElem, int attrToFind) {
        Resources.Theme theme = context.getTheme();

        TypedArray a = theme.obtainStyledAttributes(attrs, attrsToFetch, defStyle, 0);
        int textAppearanceStyle = -1;
        try {
            textAppearanceStyle = a.getResourceId(0, -1);
        } catch (Exception e) {
            Timber.e(e.toString());
        } finally {
            a.recycle();
        }

        TypedArray appearance = null;
        if (textAppearanceStyle != -1) {
            appearance = theme.obtainStyledAttributes(textAppearanceStyle, styleableElem);
        }

        return getDefaultFontAttributes(appearance, attrToFind);
    }

    private static String getDefaultFontAttributes(final TypedArray a, int attrToFind) {
        if (a == null) {
            return null;
        }
        String font = null;
        try {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == attrToFind) {
                    font = a.getString(attr);
                    break;
                }
            }
        } catch (Exception e) {
            Timber.e(e.toString());
        } finally {
            a.recycle();
        }

        return font;
    }

    private TypeFaceUtils() {
    }
}
