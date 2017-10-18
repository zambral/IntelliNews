package news.intelli.intellinews.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import news.intelli.intellinews.BuildConfig;
import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.mvp.model.User;
import news.intelli.intellinews.mvp.view.LoginView;
import news.intelli.intellinews.utils.GMTController;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by llefoulon on 25/10/2016.
 */

public class LoginPresenter extends Presenter<LoginView> implements Observer<User> {

    //TODO create state
    @IntDef({VOID, CHECK_USER_IS_LOGGED, LOGGIN_USER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginState {
    }

    private static final int VOID = 0;
    private static final int CHECK_USER_IS_LOGGED = 1;
    private static final int LOGGIN_USER = 2;

    @LoginState
    private int currentState = VOID;

    public LoginPresenter(LoginView v) {
        super(v);
    }

    public void checkUserLogged() {

        LoginView view = this.getView();
        Context context;
        if (view != null && (context = view.getMVPViewContext()) != null) {
            view.onStartProcessing();
            currentState = CHECK_USER_IS_LOGGED;
            subscription = Observable
                    .just(context)
                    .flatMap(new Func1<Context, Observable<User>>() {
                        @Override
                        public Observable<User> call(Context ctx) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            User user = User.create(firebaseUser);
                            return Observable.just(user);

                            /*SharedPreferences sharedPreferences = ctx.getSharedPreferences(IntelliNewsApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                            String userJSONString = sharedPreferences.getString(User.USER, null);
                            if (userJSONString != null) {
                                Gson gson = new Gson();
                                User user = gson.fromJson(userJSONString, User.class);
                                return Observable.just(user);
                            }
                            return Observable.just(null);*/
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    public void logUser(String email, String password) {
        String[] params = new String[2];
        params[0] = email;
        params[1] = password;

        final LoginView view = this.getView();
        if (view != null)
            view.onStartProcessing();

        currentState = LOGGIN_USER;

        subscription = Observable.just(params)
                .flatMap(new Func1<String[], Observable<User>>() {
                    @Override
                    public Observable<User> call(String[] strings) {
                        String email = strings[0];
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.WRONG_EMAIL_FORMAT));
                        }
                        String password = strings[1];
                        if (TextUtils.isEmpty(password.trim())) {
                            return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.WRONG_PASSWORD));
                        }


                        /*FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginPresenter.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                    }
                                });
                        */
                        if(BuildConfig.DEBUG) {
                            //fake verification process
                            if(BuildConfig.EMAIL.equalsIgnoreCase(email) &&
                                    BuildConfig.PASSWORD.equalsIgnoreCase(password)) {
                                //create fake User
                                User user = User.createFakeUser(email);
                                Context ctx = (view != null) ? view.getMVPViewContext() : null;
                                if(ctx != null) {
                                    SharedPreferences sharedPreferences = ctx.getSharedPreferences(IntelliNewsApplication.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(User.USER,new Gson().toJson(user));
                                    editor.apply();
                                }
                                return Observable.just(user);
                            } else {
                                return Observable.error(new ThrowableWithErrorCode(ThrowableWithErrorCode.NO_ACCOUNT));
                            }
                        } else {

                        }

                        return Observable.just(null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(this);
    }


    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        LoginView view = getView();
        if (view != null)
            view.onLoginError(e);
    }

    @Override
    public void onNext(User user) {
        GMTController.Builder builder = new GMTController.Builder();
        builder.addCategoryAndSubCategory(null, null)
                .addSpecificInfo(null, null)
                .send();
        LoginView view = getView();
        if (view != null) {
            if (currentState == CHECK_USER_IS_LOGGED)
                view.userLoginState(user != null);
            else if (currentState == LOGGIN_USER)
                view.onLoginSucceed(user);

            currentState = VOID;
        }
    }
}
