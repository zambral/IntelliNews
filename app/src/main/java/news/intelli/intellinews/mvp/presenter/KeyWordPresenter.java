package news.intelli.intellinews.mvp.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.database.IntelliNewsContentProvider;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.view.KeyWordView;
import news.intelli.intellinews.utils.RxUtils;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by llefoulon on 12/11/2016.
 */

public class KeyWordPresenter extends Presenter<KeyWordView> implements Observer<List<Keyword>> {

    public static final String TAG = Keyword.class.getName();

    public void fetchKeywordsWithValue(final String s) {
        if (subscription != null)
            subscription.unsubscribe();

        KeyWordView v = getView();
        ContentResolver contentResolver = null;
        if(v != null) {
            Context ctx = v.getMVPViewContext();
            contentResolver = ctx.getContentResolver();
        }

        subscription = Observable.just(contentResolver)
                .flatMap(new Func1<ContentResolver, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(ContentResolver contentResolver) {
                        if(contentResolver == null)
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));

                        Cursor cursor = contentResolver.query(
                                DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.KEYWORD_CONTENT_URI.toString(), s),
                                null,
                                null,
                                null,
                                null);
                        if (cursor == null) {
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));
                        }
                        return Observable.just(cursor);
                    }
                })
                .flatMap(new Func1<Cursor, Observable<ArrayList<Keyword>>>() {
                    @Override
                    public Observable<ArrayList<Keyword>> call(Cursor cursor) {
                        int amount = cursor.getCount();
                        ArrayList<Keyword> keywords = new ArrayList<>(1);
                        if (amount > 0) {
                            Keyword k = null;
                            cursor.moveToFirst();
                            for (int i = 0; i < amount; ++i) {
                                k = Keyword.generate(cursor);
                                if (k != null)
                                    keywords.add(k);

                            }
                            cursor.close();
                            return Observable.just(keywords);
                        }
                        return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public void fetchEveryKeywords() {
        if (subscription != null)
            subscription.unsubscribe();

        KeyWordView view = getView();
        ContentResolver contentResolver = null;

        if (view != null) {
            view.onStartProcessing();
            Context ctx = view.getMVPViewContext();
            contentResolver = (ctx != null) ? ctx.getContentResolver() : null;

        }

        //TODO is it better to do like this
        //IntelliNewsApplication.getInstance().getContentResolver();

        //http://stackoverflow.com/questions/3491747/which-thread-runs-contentprovider
        //https://developer.android.com/guide/topics/providers/content-provider-basics.html
        subscription = Observable.just(contentResolver)
                .flatMap(new Func1<ContentResolver, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(ContentResolver contentResolver) {
                        Cursor cursor = contentResolver.query(
                                Uri.parse(IntelliNewsContentProvider.KEYWORD_CONTENT_URI.toString()),
                                null,
                                null,
                                null,
                                Keyword.KEYWORD_TABLE_POPULARITY);
                        if (cursor == null) {
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));
                        }
                        return Observable.just(cursor);
                    }
                })
                .flatMap(RxUtils.cursorToKeywordList())
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

    }

    public void registerKeyword(final Keyword keyword) {
        KeyWordView view = getView();
        ContentResolver contentResolver = null;
        if (view != null) {
            view.onStartProcessing();
            Context ctx = view.getMVPViewContext();
            contentResolver = (ctx != null) ? ctx.getContentResolver() : null;
        }

        Timber.d("registerKeyword %s",keyword);
        Observable.just(contentResolver)
                .flatMap(new Func1<ContentResolver, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(ContentResolver contentResolver) {
                        if(contentResolver == null)
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));

                        Keyword copyKeyword = new Keyword(keyword);
                        copyKeyword.setIsRegistered(true);

                        Uri uri = contentResolver.insert(
                                DatabaseHelper.getUriForSpecificData(
                                        IntelliNewsContentProvider.KEYWORD_CONTENT_URI.toString(),
                                        keyword.getValue()),
                                        copyKeyword.toContentValues(DatabaseHelper.INSERT,null)
                        );

                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                                .getInstance(IntelliNewsApplication.getInstanceContext());
                        Intent intent = new Intent(Keyword.INSERT);

                        if (uri != null) {
                            intent.putExtra(Keyword.INSERT, keyword.getValue());
                            intent.setData(uri);
                        }

                        if (localBroadcastManager != null)
                            localBroadcastManager.sendBroadcast(intent);
                        return Observable.just(null);

                    }
                })
                .observeOn(Schedulers.io())
                .subscribe();

    }

    public void unRegisterKeyword(Keyword keyword) {
        Timber.d("registerKeyword %s",keyword);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        KeyWordView view = getView();
        if (view != null)
            view.onFetchingKeywordsError(e);
    }

    @Override
    public void onNext(List<Keyword> keywords) {
        KeyWordView view = getView();
        if (view != null)
            view.onFetchingKeywordsSucceed(keywords);
    }

}
