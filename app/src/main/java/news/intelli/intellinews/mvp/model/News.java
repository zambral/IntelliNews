package news.intelli.intellinews.mvp.model;

import android.content.ContentValues;
import android.support.annotation.DrawableRes;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.interfaces.ContentValuable;

import static news.intelli.intellinews.database.DatabaseHelper.COLUMN_ID;

/**
 * Created by llefoulon on 05/11/2016.
 */

public abstract class News implements ContentValuable {

    public static final String DATABASE_TABLE_NEWS = "news";
    public static final String NEWS_TABLE_SERVER_ID = "serverID";
    public static final String NEWS_TABLE_TITLE = "title";
    public static final String NEWS_TABLE_DESC = "desc";
    public static final String NEWS_TABLE_SOURCE = "source";
    public static final String NEWS_TABLE_URL = "url";
    public static final String NEWS_TABLE_DATE = "date";
    public static final String NEWS_LIST_THUMBNAIL_PICTURE = "listThumbnailPicture";
    public static final String NEWS_PAGE_CONTENT = "pageContent";
    public static final String NEWS_PICTURES = "pictures";
    public static final String NEWS_READ_DATE = "readDate";
    public static final String NEWS_READ_COUNT = "readCount";

    public static final String DATABASE_CREATE_NEWS_TABLE = "create table if not exists "
            + DATABASE_TABLE_NEWS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + NEWS_TABLE_SERVER_ID + " text not null,"
            + NEWS_TABLE_TITLE + " text, "
            + NEWS_TABLE_DESC + " text, "
            + NEWS_TABLE_SOURCE + " text not null, "
            + NEWS_TABLE_URL + " text not null, "
            + NEWS_TABLE_DATE + " text not null, "
            + NEWS_LIST_THUMBNAIL_PICTURE + " text, "
            + NEWS_PAGE_CONTENT + " text, "
            + NEWS_PICTURES + " text, "
            + NEWS_READ_DATE + " integer, "
            + NEWS_READ_COUNT + " integer "
            + ");";

    public static final Comparator<News> DATE_COMPARATOR = new Comparator<News>() {
        @Override
        public int compare(News news1, News news2) {
            return Long.compare(news1.getTimestamp(), news2.getTimestamp());
        }
    };

    private int readCount = 0;


    protected static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static String TO_STRING_PATTERN = "[%s] %s - %s";

    public abstract String getID();

    public abstract String getTitle();

    public abstract String getDesc();

    public abstract String getSource();

    @DrawableRes
    public abstract int getDefaultThumbnailDrawableID();

    public abstract String getThumbnailImagePath();

    public abstract String getImagePath();

    public abstract long getTimestamp();

    public abstract String getURL();

    //public abstract String getMainKeyWord();

    public abstract String[] getKeywords();

    public int getReadCount() {
        return readCount;
    }

    @Override
    public String toString() {
        return String.format(News.TO_STRING_PATTERN, getSource(), getID(), getTitle());
    }

    @Override
    public ContentValues toContentValues(@DatabaseHelper.DatabaseOperationType int type, String... fields) {
        ContentValues contentValues = new ContentValues();
        if (type == DatabaseHelper.INSERT) {
            fields = new String[8];
            fields[0] = NEWS_TABLE_SERVER_ID;
            fields[1] = NEWS_TABLE_TITLE;
            fields[2] = NEWS_TABLE_DESC;
            fields[3] = NEWS_TABLE_SOURCE;
            fields[4] = NEWS_TABLE_URL;
            fields[5] = NEWS_LIST_THUMBNAIL_PICTURE;
            fields[6] = NEWS_TABLE_DATE;
            fields[7] = NEWS_READ_COUNT;
        }

        String field;
        for (int i = 0, size = fields != null ? fields.length : 0; i < size; ++i) {
            switch (field = fields[i]) {
                case NEWS_TABLE_SERVER_ID:
                    contentValues.put(field, getID());
                    break;
                case NEWS_TABLE_TITLE:
                    contentValues.put(field, getTitle());
                    break;
                case NEWS_TABLE_DESC:
                    contentValues.put(field, getDesc());
                    break;
                case NEWS_TABLE_SOURCE:
                    contentValues.put(field, getSource());
                    break;
                case NEWS_TABLE_URL:
                    contentValues.put(field, getURL());
                    break;
                case NEWS_LIST_THUMBNAIL_PICTURE:
                    contentValues.put(field, getThumbnailImagePath());
                    break;
                case NEWS_TABLE_DATE:
                    contentValues.put(field, getTimestamp());
                    break;
                case NEWS_READ_COUNT:
                    contentValues.put(field, readCount);
                    break;
            }
        }

        return contentValues;
    }

}
