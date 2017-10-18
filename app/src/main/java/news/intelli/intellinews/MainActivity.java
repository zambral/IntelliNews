package news.intelli.intellinews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import news.intelli.intellinews.mvp.model.User;
import news.intelli.intellinews.mvp.presenter.LoginPresenter;
import news.intelli.intellinews.mvp.view.LoginView;

/**
 * This is activity is only here to manage where the user should go
 * If Logged -> NewsActivity
 * If Not Log -> Login Activity (Login + Sign in)
 */
public class MainActivity extends AppCompatActivity implements LoginView {

    private LoginPresenter provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        setContentView(R.layout.activity_main);

        provider = new LoginPresenter(this);

        //check user is log in background an redirect
        provider.checkUserLogged();
    }


    @Override
    public void onDestroy() {

        provider.removeView(this);
        provider = null;

        super.onDestroy();
    }

    @Override
    public void onLoginSucceed(User user) {}

    @Override
    public void onLoginError(Throwable e) {}

    @Override
    public void userLoginState(boolean isLogged) {
        Intent intent = new Intent(this, isLogged ? NewsActivity.class : LoginActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    public Context getMVPViewContext() {
        return this;
    }

    @Override
    public void onStartProcessing() {}

}
