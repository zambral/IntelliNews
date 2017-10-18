package news.intelli.intellinews.mvp.view;


import news.intelli.intellinews.mvp.model.User;

/**
 * Created by llefoulon on 25/10/2016.
 */

public interface LoginView extends View {
    void onLoginSucceed(User user);
    void onLoginError(Throwable e);

    void userLoginState(boolean isLogged);

}
