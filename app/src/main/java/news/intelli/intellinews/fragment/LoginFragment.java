package news.intelli.intellinews.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.LoginActivity;
import news.intelli.intellinews.NewsActivity;
import news.intelli.intellinews.R;
import news.intelli.intellinews.mvp.model.User;
import news.intelli.intellinews.mvp.presenter.LoginPresenter;
import news.intelli.intellinews.mvp.view.LoginView;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;


/**
 * Created by llefoulon on 31/10/2016.
 */

public class LoginFragment extends Fragment implements View.OnFocusChangeListener,
        TextView.OnEditorActionListener,
        View.OnClickListener,
        LoginView {

    @BindView(R.id.fragment_login_edittext)
    protected EditText emailEditText;

    @BindView(R.id.fragment_password_edittext)
    protected EditText passwordEditText;

    @BindView(R.id.fragment_login_validate_button)
    protected Button validateButton;

    @BindView(R.id.fragment_login_password_input_layout)
    protected TextInputLayout passwordTextInput;

    @BindView(R.id.fragment_login_login_input_layout)
    protected TextInputLayout emailTextInput;

    private Unbinder unbinder;

    private TextWatcher textWatcher = null;
    private LoginPresenter presenter = null;

    public static Fragment newInstance(Bundle bundle) {
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new LoginPresenter(this);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateValidateButtonState();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        unbinder = ButterKnife.bind(this, view);

        emailEditText.setOnFocusChangeListener(this);
        emailEditText.addTextChangedListener(textWatcher);
        //emailEditText.addTextChangedListener(this);

        passwordEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnEditorActionListener(this);
        passwordEditText.addTextChangedListener(textWatcher);

        validateButton.setOnClickListener(this);

        return view;
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onFocusChange(View view, final boolean b) {
        if (b) {
            setErrorForTextInput(emailTextInput, null, false);
            setErrorForTextInput(passwordTextInput, null, false);

            Intent intent = new Intent(LoginActivity.ELEMENT_HAS_FOCUS);
            intent.putExtra(LoginActivity.ELEMENT_HAS_FOCUS, b);

            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        } else if (view.getId() == R.id.fragment_password_edittext) {

            Intent intent = new Intent(LoginActivity.ELEMENT_HAS_FOCUS);
            intent.putExtra(LoginActivity.ELEMENT_HAS_FOCUS, b);

            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        }
    }

    private void updateValidateButtonState() {
        validateButton.setEnabled(!TextUtils.isEmpty(emailEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(passwordEditText.getText().toString().trim()));
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(textView);
            textView.clearFocus();
            logUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
        return false;
    }

    @Override
    public void onDestroyView() {

        emailEditText.removeTextChangedListener(textWatcher);
        passwordEditText.removeTextChangedListener(textWatcher);
        textWatcher = null;

        if (unbinder != null)
            unbinder.unbind();


        super.onDestroyView();
    }

    private void logUser(String email, String password) {
        presenter.logUser(email, password);
    }

    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        if (view.getId() == R.id.fragment_login_validate_button) {
            logUser(emailEditText.getText().toString(), passwordEditText.getText().toString());


        }
    }

    @Override
    public void onLoginSucceed(User user) {
        sendIsLoginSignal(false);
        Toast.makeText(IntelliNewsApplication.getInstanceContext(), getString(R.string.welcome, user.getEmail()), Toast.LENGTH_SHORT).show();

        Activity activity = getActivity();
        Intent intent = new Intent(activity, NewsActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void onLoginError(Throwable e) {
        sendIsLoginSignal(false);
        if (e instanceof ThrowableWithErrorCode) {
            int errorCode = ((ThrowableWithErrorCode) e).getErrorCode();
            switch (errorCode) {
                case ThrowableWithErrorCode.GENERIC_ERROR:
                    passwordTextInput.setError(getString(R.string.error_generic));
                    break;
                case ThrowableWithErrorCode.NO_ACCOUNT:
                    passwordTextInput.setError(getString(R.string.error_no_account));
                    break;
                case ThrowableWithErrorCode.WRONG_EMAIL_FORMAT:
                    emailTextInput.setError(getString(R.string.error_not_an_email));
                    break;
                case ThrowableWithErrorCode.WRONG_PASSWORD:
                    passwordTextInput.setError(getString(R.string.error_wrong_password));
                    break;
            }
        } else {
            String s = e.getMessage();
            if (s == null)
                s = getString(R.string.error_generic);

            passwordTextInput.setError(s);
        }
    }

    @Override
    public void userLoginState(boolean isLogged) {}


    @Override
    public Context getMVPViewContext() {
        return getActivity();
    }

    private static void setErrorForTextInput(TextInputLayout textInputLayout, String s, boolean errorEnabled) {
        textInputLayout.setError(s);
        if (!errorEnabled)
            textInputLayout.setErrorEnabled(false);
    }

    private void sendIsLoginSignal(boolean isLogin) {
        Intent intent = new Intent(LoginActivity.IS_PROCESSING);
        intent.putExtra(LoginActivity.IS_PROCESSING, isLogin);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onStartProcessing() {
        sendIsLoginSignal(true);
        validateButton.setEnabled(false);
        setErrorForTextInput(emailTextInput, null, false);
        setErrorForTextInput(passwordTextInput, null, false);
    }
}
