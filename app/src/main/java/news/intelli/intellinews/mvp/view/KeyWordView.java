package news.intelli.intellinews.mvp.view;

import java.util.List;

import news.intelli.intellinews.mvp.model.Keyword;

/**
 * Created by llefoulon on 12/11/2016.
 */

public interface KeyWordView extends View {
    void onFetchingKeywordsError(Throwable e);
    void onFetchingKeywordsSucceed(List<Keyword> keywords);
}
