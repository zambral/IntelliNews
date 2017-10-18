package news.intelli.intellinews.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.model.WikipediaDefinition;
import news.intelli.intellinews.utils.Converter;
import news.intelli.intellinews.utils.StringUtils;
import timber.log.Timber;

/**
 * Created by llefoulon on 12/11/2016.
 */
//https://www.tutorialspoint.com/android/android_content_providers.htm
//http://www.vogella.com/tutorials/AndroidSQLite/article.html
//http://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/
public class IntelliNewsContentProvider extends ContentProvider {

    // Creates a UriMatcher object.
    //private static final String BASE_PATH = "intellinews";
    private static final String CONTENT_URI_PATTERN = "content://%s/%s";
    public static final String AUTHORITY = "news.intelli.intellinews.database";

    private static final int PROVIDER_KEYWORD_ALL = 1;
    private static final int PROVIDER_KEYWORD_WITH_ID = 2;
    private static final int PROVIDER_DEFINITION_ALL = 3;
    private static final int PROVIDER_DEFINITION_WITH_WORD = 4;
    private static final int PROVIDER_NEWS_WITH_ID = 5;
    private static final int PROVIDER_NEWS_N_KEYWORD_WITH_IDS = 6;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    //public static final CountDownLatch DEFINITION_CONTENT_URI = ;

    static {
        uriMatcher.addURI(AUTHORITY, Keyword.DATABASE_TABLE_KEYWORD, PROVIDER_KEYWORD_ALL);
        //http://stackoverflow.com/questions/29430737/urimatcher-uri-and-difference
        //# numbers ,//* any texts
        uriMatcher.addURI(AUTHORITY, Keyword.DATABASE_TABLE_KEYWORD + "/*", PROVIDER_KEYWORD_WITH_ID);
        uriMatcher.addURI(AUTHORITY, WikipediaDefinition.DATABASE_TABLE_DEFINITION, PROVIDER_DEFINITION_ALL);
        uriMatcher.addURI(AUTHORITY, WikipediaDefinition.DATABASE_TABLE_DEFINITION + "/*", PROVIDER_DEFINITION_WITH_WORD);
        uriMatcher.addURI(AUTHORITY, News.DATABASE_TABLE_NEWS + "/*", PROVIDER_NEWS_WITH_ID);
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS + "/*/*", PROVIDER_NEWS_N_KEYWORD_WITH_IDS);
    }

    public static final Uri KEYWORD_CONTENT_URI = Uri.parse(String.format(CONTENT_URI_PATTERN,
            AUTHORITY,
            Keyword.DATABASE_TABLE_KEYWORD));

    public static final Uri DEFINITION_CONTENT_URI = Uri.parse(String.format(CONTENT_URI_PATTERN,
            AUTHORITY,
            WikipediaDefinition.DATABASE_TABLE_DEFINITION));

    public static final Uri NEWS_CONTENT_URI = Uri.parse(String.format(CONTENT_URI_PATTERN,
            AUTHORITY,
            News.DATABASE_TABLE_NEWS));

    public static final Uri NEWS_N_KEYWORD_CONTENT_URI = Uri.parse(String.format(CONTENT_URI_PATTERN,
            AUTHORITY,
            DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS));


    private DatabaseHelper openHelper;


    @Override
    public boolean onCreate() {
        openHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case PROVIDER_KEYWORD_ALL:
                queryBuilder.setTables(Keyword.DATABASE_TABLE_KEYWORD);
                break;

            case PROVIDER_KEYWORD_WITH_ID:
                String keywordValue = uri.getLastPathSegment();

                queryBuilder.setTables(Keyword.DATABASE_TABLE_KEYWORD);
                queryBuilder.appendWhere(Keyword.KEYWORD_TABLE_KEYWORD_COLUMN + "=" + keywordValue);
                break;

            case PROVIDER_DEFINITION_ALL:
                queryBuilder.setTables(WikipediaDefinition.DATABASE_TABLE_DEFINITION);
                break;
            case PROVIDER_DEFINITION_WITH_WORD:
                String def = uri.getLastPathSegment();

                queryBuilder.setTables(WikipediaDefinition.DATABASE_TABLE_DEFINITION);
                queryBuilder.appendWhere(String.format("%s = '%s'", WikipediaDefinition.DEF_TABLE_KEYWORD_COLUMN, def));
                //queryBuilder.appendWhere(String.format("%s LIKE %%%s%%",Keyword.KEYWORD_TABLE_KEYWORD_COLUMN,def));
                break;
            case PROVIDER_NEWS_WITH_ID:
                //extract id
                String uriString = uri.toString();
                String newsID = uriString.replace(NEWS_CONTENT_URI.toString(), "");
                queryBuilder.setTables(News.DATABASE_TABLE_NEWS);
                queryBuilder.appendWhere(String.format("%s = '%s'", DatabaseHelper.COLUMN_ID, newsID));
                break;
            case PROVIDER_NEWS_N_KEYWORD_WITH_IDS:
                List<String> pathSegments = uri.getPathSegments();
                int size = pathSegments.size();
                String sourceString = pathSegments.get(size - 1);
                String keywordString = pathSegments.get(size - 2);

                sourceString = StringUtils.decodeString(sourceString, "UTF-8");
                if (sourceString == null) return null;


                queryBuilder.setTables(
                        News.DATABASE_TABLE_NEWS +
                                " INNER JOIN " +
                                DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS +
                                " ON " +
                                DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS_NEWS_FOREIGN_KEY + " = " +  DatabaseHelper.COLUMN_ID +
                                " INNER JOIN " +
                                Keyword.DATABASE_TABLE_KEYWORD +
                                " ON " +
                                DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS_KEYWORD_FOREIGN_KEY + " = " + DatabaseHelper.COLUMN_ID
                );


                queryBuilder.appendWhere(String.format("%s.%s = '%s' AND %s.%s = '%s'",
                        Keyword.DATABASE_TABLE_KEYWORD,
                        Keyword.KEYWORD_TABLE_KEYWORD_COLUMN,
                        keywordString,
                        News.DATABASE_TABLE_NEWS,
                        News.NEWS_TABLE_SOURCE,
                        sourceString)
                );

                break;
            default:
                return null;
        }

        Cursor c = null;
        try {
            c = queryBuilder.query(openHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        } catch (Exception e) {
            Timber.e(e.toString());
        }

        Context ctx = getContext();
        ContentResolver contentResolver;
        if (ctx != null && ((contentResolver = ctx.getContentResolver()) != null) &&
                c != null) {
            c.setNotificationUri(contentResolver, uri);
        }

        return c;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case PROVIDER_KEYWORD_WITH_ID: {
                long id = openHelper.getWritableDatabase().insert(Keyword.DATABASE_TABLE_KEYWORD, null, contentValues);
                return DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.KEYWORD_CONTENT_URI.toString(), Long.toString(id));
            }
            case PROVIDER_DEFINITION_WITH_WORD: {
                long id = openHelper.getWritableDatabase().insert(WikipediaDefinition.DATABASE_TABLE_DEFINITION, null, contentValues);
                return DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.DEFINITION_CONTENT_URI.toString(), Long.toString(id));
            }
            case PROVIDER_NEWS_WITH_ID: {
                long id = openHelper.getWritableDatabase().insert(News.DATABASE_TABLE_NEWS, null, contentValues);
                return DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.NEWS_CONTENT_URI.toString(), Long.toString(id));
            }
            case PROVIDER_NEWS_N_KEYWORD_WITH_IDS: {
                List<String> pathSegments = uri.getPathSegments();
                int size = pathSegments.size();
                String stringNewsID = pathSegments.get(size - 1);
                String stringKeywordID = pathSegments.get(size - 2);

                if (Converter.isInt(stringKeywordID) &&
                        Converter.isInt(stringKeywordID)) {

                    contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS_KEYWORD_FOREIGN_KEY, stringKeywordID);
                    contentValues.put(DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS_NEWS_FOREIGN_KEY, stringNewsID);
                    long id = openHelper.getWritableDatabase().insert(DatabaseHelper.DATABASE_NEWS_AND_KEYWORDS,
                            null,
                            contentValues);

                    return DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.NEWS_N_KEYWORD_CONTENT_URI.toString(),
                            Long.toString(id));
                }
                return null;

            }
            default:
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] selectionArgs) {
        int delCount = 0;
        switch (uriMatcher.match(uri)) {
            case PROVIDER_KEYWORD_WITH_ID:
                int id = Converter.string2Int(uri.getLastPathSegment());
                if (id != Integer.MIN_VALUE) {
                    String where = DatabaseHelper.COLUMN_ID + " = " + id;
                    delCount = openHelper
                            .getWritableDatabase()
                            .delete(
                                    Keyword.DATABASE_TABLE_KEYWORD,
                                    where,
                                    selectionArgs);
                }
                break;
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] selectionArgs) {
        int updateCount = 0;
        switch (uriMatcher.match(uri)) {
            case PROVIDER_KEYWORD_WITH_ID:
                int id = Converter.string2Int(uri.getLastPathSegment());
                if (id != Integer.MIN_VALUE) {
                    String where = DatabaseHelper.COLUMN_ID + " = " + uri.getLastPathSegment();
                    updateCount = openHelper
                            .getWritableDatabase()
                            .update(
                                    Keyword.DATABASE_TABLE_KEYWORD,
                                    contentValues,
                                    where,
                                    selectionArgs);
                }
                break;
        }

        return updateCount;

    }
}
