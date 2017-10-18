package news.intelli.intellinews.interfaces;

import android.content.ContentValues;


import news.intelli.intellinews.database.DatabaseHelper;

/**
 * Created by llefoulon on 13/11/2016.
 */

public interface ContentValuable {

    ContentValues toContentValues(@DatabaseHelper.DatabaseOperationType int type,String... fields);
    //Object dataMapper(String field);
}
