package news.intelli.intellinews.mvp.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.interfaces.ContentValuable;

import static news.intelli.intellinews.database.DatabaseHelper.COLUMN_ID;

/**
 * Created by llefoulon on 12/11/2016.
 */

public class Keyword implements Parcelable, ContentValuable {
    public static final String INSERT = "insert";
    public static final String DATABASE_TABLE_KEYWORD = "keyword";
    public static final String KEYWORD_TABLE_KEYWORD_COLUMN = "keyword";
    public static final String KEYWORD_TABLE_POPULARITY = "popularity";
    public static final String KEYWORD_TABLE_IS_REGISTER = "registered";

    public static final String DATABASE_CREATE_KEYWORD_TABLE = "create table if not exists "
            + DATABASE_TABLE_KEYWORD
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + KEYWORD_TABLE_KEYWORD_COLUMN + " text not null, "
            + KEYWORD_TABLE_POPULARITY + " integer not null, "
            + KEYWORD_TABLE_IS_REGISTER + " integer not null"
            + ");";

    public static final Creator<Keyword> CREATOR = new Creator<Keyword>() {
        @Override
        public Keyword createFromParcel(Parcel in) {
            return new Keyword(in);
        }

        @Override
        public Keyword[] newArray(int size) {
            return new Keyword[size];
        }
    };

    private static Keyword generateKeyWord(String s) {
        Keyword keyWord = new Keyword();

        keyWord.value = s;
        keyWord.id = -1;
        keyWord.popularity = 0;
        keyWord.isRegistered = false;

        return keyWord;
    }

    private int id;
    private String value;
    private int popularity;
    private boolean isRegistered;

    private Keyword() {
    }

    public Keyword(Keyword keyword) {
        this.id = keyword.id;
        this.value = keyword.value;
        this.popularity = keyword.popularity;
        this.isRegistered = keyword.isRegistered;
    }

    protected Keyword(Parcel in) {
        id = in.readInt();
        value = in.readString();
        popularity = in.readInt();
        isRegistered = in.readByte() != 0;
    }

    public static Keyword generate(Cursor cursor) {
        Keyword keyword = new Keyword();
        keyword.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        keyword.value = cursor.getString(cursor.getColumnIndex(KEYWORD_TABLE_KEYWORD_COLUMN));
        keyword.popularity = cursor.getInt(cursor.getColumnIndex(KEYWORD_TABLE_POPULARITY));
        keyword.isRegistered = cursor.getInt(cursor.getColumnIndex(KEYWORD_TABLE_IS_REGISTER)) == 1;

        return keyword;
    }

    public String getValue() {
        return value;
    }

    public static List<Keyword> generatesTutorialKeyword() {
        List<Keyword> keywords = new ArrayList<>(3);
        keywords.add(Keyword.generateKeyWord("android"));
        keywords.add(Keyword.generateKeyWord("iOS"));
        keywords.add(Keyword.generateKeyWord("smartphone"));
        return keywords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(value);
        parcel.writeInt(popularity);
        parcel.writeByte((byte) (isRegistered ? 1 : 0));
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean b) {
        this.isRegistered = b;
    }

    @Override
    public String toString() {
        return value;
    }

    /*public Object dataMapper(String field) {
        switch (field) {
            case COLUMN_ID:
                return id;
            case KEYWORD_TABLE_KEYWORD_COLUMN:
                return value;
            case KEYWORD_TABLE_POPULARITY:
                return popularity;
            case KEYWORD_TABLE_IS_REGISTER:
                return isRegistered;
            default:
                return null;
        }

    }*/



    @Override
    public ContentValues toContentValues(@DatabaseHelper.DatabaseOperationType int type,String... fields) {
        if(type == DatabaseHelper.INSERT) {
            fields = new String[3];
            fields[0] = Keyword.KEYWORD_TABLE_KEYWORD_COLUMN;
            fields[1] = Keyword.KEYWORD_TABLE_IS_REGISTER;
            fields[2] = Keyword.KEYWORD_TABLE_POPULARITY;
        }
        ContentValues contentValues = new ContentValues();
        String field;
        for(int i = 0, size = fields != null ? fields.length : 0;i < size;++i) {
            switch (field = fields[i]) {
                case COLUMN_ID:
                    contentValues.put(field,id);
                    break;
                case KEYWORD_TABLE_KEYWORD_COLUMN:
                    contentValues.put(field,value);
                    break;
                case KEYWORD_TABLE_POPULARITY:
                    contentValues.put(field,popularity);
                    break;
                case KEYWORD_TABLE_IS_REGISTER:
                    contentValues.put(field,isRegistered);
                    break;
            }
        }

        return contentValues;
    }


}
