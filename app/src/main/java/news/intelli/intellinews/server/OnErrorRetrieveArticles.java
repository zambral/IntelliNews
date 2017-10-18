package news.intelli.intellinews.server;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.database.IntelliNewsContentProvider;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.utils.StringUtils;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by llefoulon on 04/12/2016.
 */

public class OnErrorRetrieveArticles implements Func1<Throwable, ArrayList<News>> {

    private String src;
    private String keyword;

    private OnErrorRetrieveArticles() {
    }

    public OnErrorRetrieveArticles(String kWord, String source) {
        src = source;
        keyword = kWord;
    }


    @Override
    public ArrayList<News> call(Throwable throwable) {
        Timber.e(throwable.getMessage());
        //TODO fetch article in database
        //http://stackoverflow.com/questions/15852348/android-sqlite-using-db-query-for-join-instead-of-rawquery
        //http://blog.cubeactive.com/android-creating-a-join-with-sqlite/

        ContentResolver contentResolver = IntelliNewsApplication.getInstanceContext().getContentResolver();

        if (contentResolver == null) return null;

        String srcEncoded = StringUtils.encodeString(src,"UTF-8");
        if(srcEncoded == null) return null;

        Cursor cursor = contentResolver.query(
                DatabaseHelper.getUriForSpecificData(
                        IntelliNewsContentProvider.NEWS_N_KEYWORD_CONTENT_URI.toString(),
                        keyword, srcEncoded
                ),
                null,
                null,
                null,
                null);

        if (cursor == null) return null;


        //return Observable.just(cursor);

        return null;
    }
}
