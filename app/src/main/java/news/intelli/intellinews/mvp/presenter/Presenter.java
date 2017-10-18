package news.intelli.intellinews.mvp.presenter;

import news.intelli.intellinews.mvp.view.View;
import rx.Subscription;

/**
 * Created by llefoulon on 25/10/2016.
 */

public class Presenter<T extends View> {
    private T view;
    protected Subscription subscription;

    public Presenter() {
        attachView(null);
    }

    public Presenter(T v) {
       attachView(v);
    }

    protected T getView() {
        return view;
    }

    public void removeView(T v) {
        if(view == v)
            view = null;
    }

    public void attachView(T v) {
        view = v;
    }
}
