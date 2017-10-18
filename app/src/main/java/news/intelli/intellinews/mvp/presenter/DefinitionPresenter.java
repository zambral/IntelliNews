package news.intelli.intellinews.mvp.presenter;

import android.content.ContentResolver;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.mvp.model.WikipediaDefinition;
import news.intelli.intellinews.mvp.view.DefinitionView;
import news.intelli.intellinews.server.APIManager;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by llefoulon on 20/11/2016.
 */

public class DefinitionPresenter extends Presenter<DefinitionView> implements Observer<List<WikipediaDefinition>> {


    public DefinitionPresenter(DefinitionView view) {
        super(view);
    }

    public void fetchDefinition(String def, boolean showLoading) {
        ContentResolver contentResolver = null;
        DefinitionView view = getView();
        if (view != null) {
            Context ctx = view.getMVPViewContext();
            contentResolver = (ctx != null) ? ctx.getContentResolver() : null;
            if (showLoading) {
                view.onStartProcessing();
            }
        }

        if (subscription != null)
            subscription.unsubscribe();

        subscription = APIManager.getInstance()
                .getDefinitions(contentResolver, def)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        DefinitionView view = getView();
        if (view != null) {
            view.onFetchDefinitionFailed(e);
        }
    }

    @Override
    public void onNext(List<WikipediaDefinition> definitions) {
        DefinitionView view = getView();
        if (view != null) {
            view.onFetchDefinitionSucceed(definitions);
        }
    }
}
