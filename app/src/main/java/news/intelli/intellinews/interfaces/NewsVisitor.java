package news.intelli.intellinews.interfaces;

import news.intelli.intellinews.mvp.model.News;

/**
 * Created by llefoulon on 07/12/2016.
 */

public interface NewsVisitor {
    void visit(News news);
}
