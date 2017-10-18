package news.intelli.intellinews.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import news.intelli.intellinews.LoginActivity;
import news.intelli.intellinews.R;

/**
 * Created by llefoulon on 31/10/2016.
 */

public class RegisterFragment extends Fragment implements View.OnFocusChangeListener, TextView.OnEditorActionListener {
    public static RegisterFragment newInstance(Bundle bundle) {
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_register,container,false);

        EditText editText = (EditText) view.findViewById(R.id.fragment_register_pseudo_editText);
        editText.setOnFocusChangeListener(this);

        editText = (EditText) view.findViewById(R.id.fragment_register_email_editText);
        editText.setOnFocusChangeListener(this);

        editText = (EditText) view.findViewById(R.id.fragment_register_password_editText);
        editText.setOnFocusChangeListener(this);
        editText.setOnEditorActionListener(this);


        return view;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b) {
            Intent intent = new Intent(LoginActivity.ELEMENT_HAS_FOCUS);
            intent.putExtra(LoginActivity.ELEMENT_HAS_FOCUS, b);

            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        } else if (view.getId() == R.id.fragment_register_password_editText) {

            Intent intent = new Intent(LoginActivity.ELEMENT_HAS_FOCUS);
            intent.putExtra(LoginActivity.ELEMENT_HAS_FOCUS, b);

            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        }
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard(textView);
            textView.clearFocus();
        }
        return false;
    }
}
