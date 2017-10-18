package news.intelli.intellinews.server;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.database.IntelliNewsContentProvider;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.News;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

import static news.intelli.intellinews.database.DatabaseHelper.COLUMN_ID;

/**
 * Created by llefoulon on 04/12/2016.
 */

public class RegisterNewsInDatabase implements Func1<List<News>, List<News>> {

    private String keyword = null;

    private RegisterNewsInDatabase() {}

    public RegisterNewsInDatabase(String kWord) {
        keyword = kWord;
    }

    private static ArrayList<ContentProviderOperation> getDatabaseOperationList(@Nullable List<News> newses) {
        int amount = newses != null ? newses.size() : 0;
        ArrayList<ContentProviderOperation> batchOps = new ArrayList<>(amount);
        News news;
        for (int index = 0; index < amount; ++index) {
            news = newses.get(index);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    DatabaseHelper.getUriForSpecificData(
                            IntelliNewsContentProvider.NEWS_CONTENT_URI.toString(),
                            news.getTitle()
                    )
            );
            builder.withValues(news.toContentValues(DatabaseHelper.INSERT, null));
            batchOps.add(builder.build());
        }

        return batchOps;
    }

    private static ContentProviderResult[] registerNewsInDatabase(@NonNull Context context, List<News> newses) {
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;

        ArrayList<ContentProviderOperation> batchOps = getDatabaseOperationList(newses);

        try {
            return contentResolver.applyBatch(IntelliNewsContentProvider.AUTHORITY, batchOps);
        } catch (Exception e) {
            Timber.e(e.toString());
            return null;
        }
    }

    private int getKeyWordID(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return -1;

        Cursor cursor = contentResolver.query(
                DatabaseHelper.getUriForSpecificData(
                        IntelliNewsContentProvider.KEYWORD_CONTENT_URI.toString(),
                        keyword
                ),
                new String[]{DatabaseHelper.COLUMN_ID},
                null,
                null,
                null
        );

        int amount = cursor != null ? cursor.getCount() : 0;
        int id = -1;
        if (amount > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }

        if (cursor != null) cursor.close();

        return id;
    }

    private static ArrayList<ContentProviderOperation> getNewsAssociatedToKeywordInsertList(ContentProviderResult[] uris,
                                                                                            int keyWordID) {
        int size = uris.length;
        ArrayList<ContentProviderOperation> batchOps = new ArrayList<>(size);
        ContentProviderResult result;
        String newsId;
        for (int i = 0; i < size; ++i) {
            result = uris[i];
            newsId = result.uri.getLastPathSegment();

            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                    DatabaseHelper.getUriForSpecificData(
                            IntelliNewsContentProvider.NEWS_N_KEYWORD_CONTENT_URI.toString(),
                            keyWordID, newsId)
            );
            builder.withValues(null);
            batchOps.add(builder.build());
        }

        return batchOps;
    }

    private boolean registerNewsAssociatedToKeyword(Context context, ContentProviderResult[] uris) {
        //fetch keyword identifier
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return false;

        int keyWordID = getKeyWordID(IntelliNewsApplication.getInstanceContext());
        if (keyWordID > 0) {
            ArrayList<ContentProviderOperation> batchOps = getNewsAssociatedToKeywordInsertList(uris, keyWordID);
            try {
                contentResolver.applyBatch(IntelliNewsContentProvider.AUTHORITY, batchOps);
                return true;
            } catch (Exception e) {
                Timber.e(e.toString());
            }
        }
        return false;
    }

    @Override
    public List<News> call(List<News> newses) {
        //
        ContentProviderResult[] uris = registerNewsInDatabase(IntelliNewsApplication.getInstanceContext(), newses);
        registerNewsAssociatedToKeyword(IntelliNewsApplication.getInstanceContext(), uris);
        return newses;
    }
}
