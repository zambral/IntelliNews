package news.intelli.intellinews.mvp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.interfaces.ContentValuable;

import static news.intelli.intellinews.database.DatabaseHelper.COLUMN_ID;

/**
 * Created by llefoulon on 13/11/2016.
 */

public class WikipediaDefinition implements ContentValuable{
    public static final String DEF_TABLE_TITLE_COLUMN = "title";
    public static final String DEF_TABLE_DESC_COLUMN = "desc";
    public static final String DEF_TABLE_URL_COLUMN = "url";
    public static final String DEF_TABLE_KEYWORD_COLUMN = "keyword";
    public static final String DATABASE_TABLE_DEFINITION = "definition";
    public static final String DATABASE_CREATE_DEFINITION_TABLE = "create table if not exists "
            + DATABASE_TABLE_DEFINITION
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + DEF_TABLE_TITLE_COLUMN + " text not null, "
            + DEF_TABLE_DESC_COLUMN + " text, "
            + DEF_TABLE_URL_COLUMN + " text not null,"
            + DEF_TABLE_KEYWORD_COLUMN + " text not null"
            + ");";

    private long id;
    private String keyword;
    private String title,desc,url;

    private WikipediaDefinition(){}

    public static WikipediaDefinition create(String stringTitle, String stringDesc, String stringURL,String keyword) {
        if(TextUtils.isEmpty(stringTitle) || TextUtils.isEmpty(stringURL))
            return null;

        WikipediaDefinition def = new WikipediaDefinition();
        def.title = stringTitle;
        def.desc = stringDesc;
        def.url = stringURL;
        def.keyword = keyword;
        return def;
    }

    public static WikipediaDefinition create(Cursor cursor) {
        if(cursor == null)
            return null;

        WikipediaDefinition def = new WikipediaDefinition();


        def.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        def.title = cursor.getString(cursor.getColumnIndex(DEF_TABLE_TITLE_COLUMN));
        def.desc = cursor.getString(cursor.getColumnIndex(DEF_TABLE_DESC_COLUMN));
        def.url = cursor.getString(cursor.getColumnIndex(DEF_TABLE_URL_COLUMN));
        def.keyword = cursor.getString(cursor.getColumnIndex(DEF_TABLE_KEYWORD_COLUMN));

        return def;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString(){
        return String.format("[%s] -> %s",keyword,title);
    }

    @Override
    public ContentValues toContentValues(@DatabaseHelper.DatabaseOperationType int type, String... fields) {
        if(type == DatabaseHelper.INSERT) {
            ContentValues values = new ContentValues();
            values.put(DEF_TABLE_TITLE_COLUMN,title);
            values.put(DEF_TABLE_DESC_COLUMN,desc);
            values.put(DEF_TABLE_URL_COLUMN,url);
            values.put(DEF_TABLE_KEYWORD_COLUMN,keyword);

            return values;
        }
        return null;
    }



}
