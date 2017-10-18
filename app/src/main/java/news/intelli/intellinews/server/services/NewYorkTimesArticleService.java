package news.intelli.intellinews.server.services;


import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.mvp.model.NYTimesArticle;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by llefoulon on 01/11/2016.
 */

public interface NewYorkTimesArticleService {

    class NYTimesArticlesResponse {
        protected ArrayList<NYTimesArticle> docs;
    }

    class NYTimesArticlesResponseContainer {
        protected NYTimesArticlesResponse response;

        public List<NYTimesArticle> getArticles() {
            return response != null ? response.docs : null;
        }
    }

    @GET("svc/search/v2/articlesearch.json")
    Observable<NYTimesArticlesResponseContainer> getArticles(@Query("q") String q,@Query("api-key") String apiKey);
}
