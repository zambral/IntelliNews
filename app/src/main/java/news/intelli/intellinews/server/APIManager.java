package news.intelli.intellinews.server;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.ArrayMap;


import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import news.intelli.intellinews.BuildConfig;
import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.database.IntelliNewsContentProvider;
import news.intelli.intellinews.mvp.model.GuardianArticle;
import news.intelli.intellinews.mvp.model.NYTimesArticle;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.model.WikipediaDefinition;
import news.intelli.intellinews.server.services.GuardianArticleService;
import news.intelli.intellinews.server.services.NewYorkTimesArticleService;
import news.intelli.intellinews.server.services.WikipediaService;
import news.intelli.intellinews.utils.RxUtils;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import news.intelli.intellinews.utils.network.NetworkUtils;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

/**
 * Created by llefoulon on 01/11/2016.
 */

public class APIManager {

    private static final String NY_TIMES_FORBIDDEN_ARTICLE_PATTERN = "topics.nytimes.com";
    private static APIManager instance = null;

    private Retrofit nyRetrofit = null;
    private Retrofit guardianRetrofit = null;
    private Retrofit wikipediaRetrofit = null;

    //TODO add cache control for request -> https://github.com/square/retrofit/issues/693
    private APIManager() {

        nyRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.NY_TIMES_SEARCH_API_BASE_URL)
                .build();

        guardianRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.GUARDIAN_SEARCH_API_BASE_URL)
                .build();

        wikipediaRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.WIKIPEDIA_API_BASE_URL)
                .build();

    }

    public static ArrayMap<String, Object> getTheGuardianQueryParams(String queryWord) {
        ArrayMap<String, Object> queryParams = new ArrayMap<>();
        queryParams.put("q", queryWord);
        queryParams.put("page-size", 10);
        queryParams.put("api-key", BuildConfig.GUARDIAN_SEARCH_API_KEY);
        queryParams.put("show-fields", "thumbnail,shortUrl");
        return queryParams;
    }

    public static APIManager getInstance() {
        if (instance == null)
            instance = new APIManager();

        return instance;
    }

    private <T> T getNYService(Class<T> className) {
        return nyRetrofit.create(className);

    }

    private <T> T getGuardianService(Class<T> className) {
        return guardianRetrofit.create(className);
    }

    private <T> T getWikipediaService(Class<T> className) {
        return wikipediaRetrofit.create(className);
    }

    private Observable<List<News>> getNYArticlesObservable(String keyword,
                                                                Func1<Throwable, ArrayList<News>> onErrorRetrieveArticles) {
        return getNYService(NewYorkTimesArticleService.class)
                .getArticles(keyword, BuildConfig.NY_TIMES_SEARCH_API_KEY)
                .flatMap(RxUtils.reponseFromNYTimesServerToNewsList())
                .flatMapIterable(RxUtils.NEWS_ARRAYLIST_TO_ITERABLE)
                .filter(new Func1<News, Boolean>() {
                    @Override
                    public Boolean call(News news) {
                        String url;
                        return (url = news.getURL()) != null && !url.contains(NY_TIMES_FORBIDDEN_ARTICLE_PATTERN);
                    }
                })
                .toList()
                .map(new RegisterNewsInDatabase(keyword))
                .onErrorReturn(onErrorRetrieveArticles);
    }

    private Observable<List<News>> getGuardiansObservableArticles(String keyword,
                                                                       OnErrorRetrieveArticles onErrorRetrieveArticles) {
        return getGuardianService(GuardianArticleService.class)
                .getArticles(APIManager.getTheGuardianQueryParams(keyword))
                .flatMap(RxUtils.responseFromGuardianServerToNewsList())
                .map(new RegisterNewsInDatabase(keyword))
                .onErrorReturn(onErrorRetrieveArticles);
    }

    public Observable<List<News>> getArticles(String keyword) {
        if (NetworkUtils.getInstance().getActiveNetworkState() == NetworkUtils.TYPE_NOT_CONNECTED)
            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.NO_NETWORK));


        Observable<List<News>> observableNYNews = getNYArticlesObservable(keyword,
                new OnErrorRetrieveArticles(keyword,NYTimesArticle.SOURCE));

        Observable<List<News>> observableTheGuardiansNews = getGuardiansObservableArticles(keyword,
                new OnErrorRetrieveArticles(keyword,GuardianArticle.SOURCE));

        return Observable.zip(observableNYNews, observableTheGuardiansNews,
                new Func2<List<News>, List<News>, List<News>>() {
                    @Override
                    public List<News> call(List<News> newses,List<News> newses2) {
                        int newsesSize = newses != null ? newses.size() : 0;
                        int newses2Size = newses != null ? newses2.size() : 0;
                        List<News> newsMerged = new ArrayList<>(newsesSize + newses2Size);
                        if (newses != null)
                            newsMerged.addAll(newses);

                        if (newses2 != null)
                            newsMerged.addAll(newses2);

                        return newsMerged;
                    }
                })
                .flatMapIterable(RxUtils.NEWS_ARRAYLIST_TO_ITERABLE)
                .toSortedList(new Func2<News, News, Integer>() {
                    @Override
                    public Integer call(News news, News news2) {
                        return News.DATE_COMPARATOR.compare(news, news2);
                    }
                });
                //.map(RxUtils.NEWS_LIST_TO_ARRAYLIST);
    }



    private Observable<List<WikipediaDefinition>> getDefinitionsFromServer(final String keyWord) {
        return getWikipediaService(WikipediaService.class)
                .getPages("opensearch", keyWord)
                .flatMap(RxUtils.responseFromServerToWikipediaObjectFunc(keyWord))
                .flatMap(RxUtils.saveDefinitionInDatabaseAndGo());
    }

    public Observable<List<WikipediaDefinition>> getDefinitions(ContentResolver contentResolver,
                                                                final String string) {
        return Observable.just(contentResolver)
                .flatMap(RxUtils.getDefinitionCursor(string))
                .flatMap(RxUtils.cursorToDefinition())
                .flatMap(new Func1<List<WikipediaDefinition>, Observable<List<WikipediaDefinition>>>() {
                    @Override
                    public Observable<List<WikipediaDefinition>> call(List<WikipediaDefinition> wikipediaDefinitions) {
                        if(wikipediaDefinitions == null || wikipediaDefinitions.isEmpty())
                            return getDefinitionsFromServer(string);

                        return null;
                    }
                });
    }
}
