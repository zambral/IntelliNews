package news.intelli.intellinews.mvp.view;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.mvp.model.WikipediaDefinition;

/**
 * Created by llefoulon on 20/11/2016.
 */

public interface DefinitionView extends View {
    void onFetchDefinitionSucceed(List<WikipediaDefinition> definitions);
    void onFetchDefinitionFailed(Throwable e);
}
