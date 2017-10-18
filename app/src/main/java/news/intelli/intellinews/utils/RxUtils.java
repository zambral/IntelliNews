package news.intelli.intellinews.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.database.DatabaseHelper;
import news.intelli.intellinews.database.IntelliNewsContentProvider;
import news.intelli.intellinews.mvp.model.GuardianArticle;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.NYTimesArticle;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.model.WikipediaDefinition;
import news.intelli.intellinews.server.services.GuardianArticleService;
import news.intelli.intellinews.server.services.NewYorkTimesArticleService;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by llefoulon on 03/12/2016.
 */

public final class RxUtils {

    public final static Func1<List<News>, Iterable<News>> NEWS_ARRAYLIST_TO_ITERABLE = new Func1<List<News>, Iterable<News>>() {
        @Override
        public Iterable<News> call(List<News> newses) {
            return newses;
        }
    };

    public final static Func1<List<News>, ArrayList<News>> NEWS_LIST_TO_ARRAYLIST = new Func1<List<News>, ArrayList<News>>() {
        @Override
        public ArrayList<News> call(List<News> newses) {
            int size = newses.size();
            if (size > 0) {
                ArrayList<News> news = new ArrayList<>(newses.size());
                news.addAll(newses);
                return news;
            }
            return null;
        }
    };

    public static Func1<ResponseBody, Observable<List<WikipediaDefinition>>> responseFromServerToWikipediaObjectFunc(final String keyWord) {
        return new Func1<ResponseBody, Observable<List<WikipediaDefinition>>>() {
            @Override
            public Observable<List<WikipediaDefinition>> call(ResponseBody responseBody) {
                try {
                    String s = responseBody.string();
                    JSONArray jsonArray = new JSONArray(s);
                    //0 -> fetched word
                    //1 -> values found on wikipedia (array)
                    //2 -> description (array)
                    //3 -> urls (array)
                    JSONArray titles = jsonArray.optJSONArray(1);
                    JSONArray desc = jsonArray.optJSONArray(2);
                    JSONArray urls = jsonArray.optJSONArray(3);
                    int len = titles != null ? titles.length() : 0;
                    WikipediaDefinition def;
                    List<WikipediaDefinition> defs = new ArrayList<>(len);
                    for (int i = 0; i < len; ++i) {
                        def = WikipediaDefinition.create(titles.optString(i), desc.optString(i), urls.optString(i), keyWord);
                        if (def != null) {
                            defs.add(def);
                        }
                    }

                    return Observable.just(defs);
                } catch (Exception e) {
                    Timber.e(e.toString());
                    return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.GENERIC_ERROR));
                }

            }
        };
    }

    private RxUtils() {
    }

    public static Func1<List<WikipediaDefinition>, Observable<List<WikipediaDefinition>>> saveDefinitionInDatabaseAndGo() {
        return new Func1<List<WikipediaDefinition>, Observable<List<WikipediaDefinition>>>() {
            @Override
            public Observable<List<WikipediaDefinition>> call(List<WikipediaDefinition> wikipediaDefinitions) {
                int amount = wikipediaDefinitions != null ? wikipediaDefinitions.size() : 0;
                if (amount > 0) {

                    //TODO : is it really necessary to make it here
                    ContentResolver contentResolver = IntelliNewsApplication.getInstance().getContentResolver();
                    //end

                    //register in database
                    ArrayList<ContentProviderOperation> batchOps = new ArrayList<>(amount);
                    // Create a set of insert ContentProviderOperations
                    WikipediaDefinition def;
                    for (int index = 0; index < amount; ++index) {
                        def = wikipediaDefinitions.get(index);
                        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                                DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.DEFINITION_CONTENT_URI.toString(),
                                        def.getTitle())
                        );
                        builder.withValues(def.toContentValues(DatabaseHelper.INSERT, null));
                        batchOps.add(builder.build());
                    }

                    // Invoke the batch insertion
                    try {
                        contentResolver.applyBatch(IntelliNewsContentProvider.AUTHORITY, batchOps);
                    } catch (Exception e) {
                        Timber.e(e.toString());
                    }
                }

                return Observable.just(wikipediaDefinitions);
            }
        };

    }

    public static Func1<GuardianArticleService.GuardianResponseContainer, Observable<List<News>>> responseFromGuardianServerToNewsList() {
        return new Func1<GuardianArticleService.GuardianResponseContainer, Observable<List<News>>>() {
            @Override
            public Observable<List<News>> call(GuardianArticleService.GuardianResponseContainer guardianResponse) {
                List<GuardianArticle> articles = guardianResponse.getArticles();

                List<News> news = null;
                if (articles != null) {
                    news = new ArrayList<>(articles.size());
                    news.addAll(articles);
                }

                return Observable.just(news);
            }
        };

    }

    public static Func1<NewYorkTimesArticleService.NYTimesArticlesResponseContainer, Observable<List<News>>> reponseFromNYTimesServerToNewsList() {
        return new Func1<NewYorkTimesArticleService.NYTimesArticlesResponseContainer, Observable<List<News>>>() {
            @Override
            public Observable<List<News>> call(NewYorkTimesArticleService.NYTimesArticlesResponseContainer nyTimesArticlesResponseContainer) {
                List<NYTimesArticle> nyArticles = nyTimesArticlesResponseContainer.getArticles();
                List<News> news = null;
                if (nyArticles != null) {
                    news = new ArrayList<>(nyArticles.size());
                    news.addAll(nyArticles);
                }

                return Observable.just(news);
            }
        };
    }

    public static Func1<ContentResolver, Observable<Cursor>> getDefinitionCursor(final String string) {
        return new Func1<ContentResolver, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(ContentResolver contentResolver) {
                if (contentResolver == null)
                    return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));

                Cursor cursor = contentResolver.query(
                        DatabaseHelper.getUriForSpecificData(IntelliNewsContentProvider.DEFINITION_CONTENT_URI.toString(), string),
                        null,
                        null,
                        null,
                        null);
                if (cursor == null) {
                    return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.DATABASE_ERROR));
                }
                return Observable.just(cursor);
            }
        };
    }

    private static List<WikipediaDefinition> createWikipediaDefinition(@NonNull Cursor cursor) {
        int amount = cursor.getCount();
        List<WikipediaDefinition> defs = new ArrayList<>(amount);
        if (amount > 0) {
            WikipediaDefinition def;
            cursor.moveToFirst();
            for (int i = 0; i < amount; ++i) {
                def = WikipediaDefinition.create(cursor);
                if (def != null)
                    defs.add(def);

                cursor.moveToNext();
            }

            return defs;
        } else
            return null;
    }

    public static Func1<Cursor, Observable<List<WikipediaDefinition>>> cursorToDefinition() {
        return new Func1<Cursor, Observable<List<WikipediaDefinition>>>() {
            @Override
            public Observable<List<WikipediaDefinition>> call(Cursor cursor) {
                if (cursor != null) {
                    List<WikipediaDefinition> defs = createWikipediaDefinition(cursor);
                    cursor.close();

                    return Observable.just(defs);
                }
                return Observable.just(null);
            }
        };
    }

    public static Func1<Cursor, Observable<List<Keyword>>> cursorToKeywordList() {
        return new Func1<Cursor, Observable<List<Keyword>>>() {
            @Override
            public Observable<List<Keyword>> call(Cursor cursor) {
                int amount = cursor.getCount();
                if (amount > 0) {
                    List<Keyword> keywords = new ArrayList<>(amount);
                    cursor.moveToFirst();
                    Keyword keyword;
                    for (int i = 0; i < amount; ++i) {
                        keyword = Keyword.generate(cursor);
                        if(keyword != null)
                            keywords.add(keyword);
                    }
                    cursor.close();
                    return Observable.just(keywords);
                }
                return Observable.just(Keyword.generatesTutorialKeyword());
            }
        };
    }
}
