package news.intelli.intellinews.mvp.presenter;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import news.intelli.intellinews.BuildConfig;
import news.intelli.intellinews.mvp.model.GuardianArticle;
import news.intelli.intellinews.mvp.model.NYTimesArticle;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.view.NewsView;
import news.intelli.intellinews.server.APIManager;
import news.intelli.intellinews.server.services.GuardianArticleService;
import news.intelli.intellinews.server.services.NewYorkTimesArticleService;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by llefoulon on 05/11/2016.
 */

public class NewsPresenter extends Presenter<NewsView> implements Observer<List<News>> {
    //private ArrayList<News> results = new ArrayList<>();

    public NewsPresenter(NewsView v) {
        super(v);
    }

    public void fetchNews(String keyWord) {
        NewsView view = getView();
        if(view != null) {
            view.onStartProcessing();
        }

        if(subscription != null)
            subscription.unsubscribe();

        //ArrayList<Observable<ArrayList<News>>> observables = getObservables(keyWord);

        /*Observable.zip(observables.iterator(), new FuncN<ArrayList<News>>() {


            @Override
            public ArrayList<News> call(Object... args) {
                return null;
            }
        });*/

        subscription = APIManager.getInstance()
                .getArticles(keyWord)
                .subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e.toString());
        NewsView view = getView();
        if(view != null) {
            view.onGetNewsFailed(e);
        }
    }

    @Override
    public void onNext(List<News> news) {
        NewsView view = getView();
        if(view != null) {
            view.onGetNewsSucceed(news);
        }
    }
}
