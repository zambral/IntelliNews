package news.intelli.intellinews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IntDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.model.WikipediaDefinition;

/**
 * Created by llefoulon on 12/11/2016.
 */
//http://www.vogella.com/tutorials/AndroidSQLite/article.html
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String SPECIFIC_DATA_PATTERN = "%s/%s";
    private static final String DATABASE_NAME = "intellinewstable.db";
    private static final int DATABASE_VERSION = 5;

    public static final String COLUMN_ID = "id";
    private static final String DROP_TABLE_PATTERN = "DROP TABLE IF EXISTS %s";

    //https://www.sqlite.org/foreignkeys.html
    public static final String DATABASE_NEWS_AND_KEYWORDS = "newsNKeywords";
    public static final String DATABASE_NEWS_AND_KEYWORDS_KEYWORD_FOREIGN_KEY = "keywordID";
    public static final String DATABASE_NEWS_AND_KEYWORDS_NEWS_FOREIGN_KEY = "newsID";
    private static final String DATABASE_CREATE_NEWS_AND_KEYWORDS =
            "CREATE TABLE " + DATABASE_NEWS_AND_KEYWORDS + "(" +
                    COLUMN_ID + " integer primary key autoincrement," +
                    DATABASE_NEWS_AND_KEYWORDS_KEYWORD_FOREIGN_KEY + " integer," +
                    DATABASE_NEWS_AND_KEYWORDS_NEWS_FOREIGN_KEY + " integer," +
                    "FOREIGN KEY(" + DATABASE_NEWS_AND_KEYWORDS_KEYWORD_FOREIGN_KEY + ") REFERENCE " +
                    Keyword.DATABASE_TABLE_KEYWORD + "(" + COLUMN_ID+ ")," +
                    "FOREIGN KEY(" + DATABASE_NEWS_AND_KEYWORDS_NEWS_FOREIGN_KEY + ") REFERENCE " +
                    News.DATABASE_TABLE_NEWS + "(" + COLUMN_ID+ ")" +
                    ");";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({INSERT, UPDATE, DELETE})
    public @interface DatabaseOperationType {}

    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //http://stackoverflow.com/questions/22791217/should-i-enable-foreign-key-constraint-in-onopen-or-onconfigure
    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {
        super.onConfigure(sqLiteDatabase);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
        } else {
            sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Keyword.DATABASE_CREATE_KEYWORD_TABLE);
        sqLiteDatabase.execSQL(WikipediaDefinition.DATABASE_CREATE_DEFINITION_TABLE);
        sqLiteDatabase.execSQL(News.DATABASE_CREATE_NEWS_TABLE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_NEWS_AND_KEYWORDS);
    }

    private static String getDropTableRawQuery(String table) {
        return String.format(DROP_TABLE_PATTERN, table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO verify if necessary
        //sqLiteDatabase.execSQL(getDropTableRawQuery(Keyword.DATABASE_TABLE_KEYWORD));
        //sqLiteDatabase.execSQL(getDropTableRawQuery(WikipediaDefinition.DATABASE_TABLE_DEFINITION));
        sqLiteDatabase.execSQL(getDropTableRawQuery(News.DATABASE_TABLE_NEWS));
        sqLiteDatabase.execSQL(getDropTableRawQuery(DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS));
        onCreate(sqLiteDatabase);
    }

    public static Uri getUriForSpecificData(String baseUri, String id) {
        return Uri.parse(String.format(SPECIFIC_DATA_PATTERN, baseUri, id));
    }

    public static Uri getUriForSpecificData(String baseUri, Object... ids) {
        if(ids == null ||ids.length == 0) return null;

        StringBuilder sB = new StringBuilder();
        for(int i = 0,size = ids.length;i < size;++i) {
            sB.append(ids[i].toString());
            sB.append(File.separator);
        }
        return Uri.parse(String.format(SPECIFIC_DATA_PATTERN, baseUri, sB.toString()));
    }
}
