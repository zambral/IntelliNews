package news.intelli.intellinews.mvp.view;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.mvp.model.News;

/**
 * Created by llefoulon on 05/11/2016.
 */

public interface NewsView extends View {

    void onGetNewsSucceed(List<News> news);
    void onGetNewsFailed(Throwable e);
}
