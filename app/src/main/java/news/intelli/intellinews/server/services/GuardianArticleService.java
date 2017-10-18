package news.intelli.intellinews.server.services;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;


import news.intelli.intellinews.mvp.model.GuardianArticle;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by llefoulon on 06/11/2016.
 */

public interface GuardianArticleService {

    class GuardianResponse {
        protected int currentPage;
        protected int startIndex;
        protected int pages;

        protected ArrayList<GuardianArticle> results;

    }

    class GuardianResponseContainer {
        protected GuardianResponse response;

        public List<GuardianArticle> getArticles(){
            return response != null ? response.results : null;
        }
    }

    @GET("search")
    Observable<GuardianResponseContainer> getArticles(@QueryMap ArrayMap<String,Object> queryParams);
}
