package news.intelli.intellinews.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.utils.network.NetworkUtils;

/**
 * Created by llefoulon on 27/11/2016.
 */

public class GMTController {

    private static final String CAT = "category";
    private static final String SUBCAT = "sub-category";
    private static final String CONECTIVITY_TYPE = "connectivity-type";

    public static class Builder {
        ArrayMap<String, Object> tags = null;

        public Builder() {
            tags = new ArrayMap<>();
            String connectivityString = NetworkUtils.getInstance().getActiveNetworkStateString();
            if (connectivityString != null) {
                tags.put(CONECTIVITY_TYPE, connectivityString);
            }
        }

        public Builder addCategoryAndSubCategory(@NonNull String categoryVal, String subCategoryVal) {
            tags.put(CAT, categoryVal);
            if(subCategoryVal != null) {
                tags.put(SUBCAT, subCategoryVal);
            }
            return this;
        }

        public Builder addSpecificInfo(@NonNull String key, @NonNull Object value) {
            tags.put(key, value);
            return this;
        }

        public void send() {
            GMTController.push(tags);
        }
    }

    public static void init(@NonNull Context context) {
    }

    private static void push(ArrayMap<String, Object> tags) {
    }
}
