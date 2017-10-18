package news.intelli.intellinews.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import news.intelli.intellinews.R;
import news.intelli.intellinews.fragment.LoginFragment;
import news.intelli.intellinews.fragment.RegisterFragment;

/**
 * Created by llefoulon on 31/10/2016.
 */

public class LoginOrRegisterPagerAdapter extends FragmentPagerAdapter {
    private static final int AMOUNT_OF_SECTIONS = 2;
    private String[] titles = null;

    public LoginOrRegisterPagerAdapter(FragmentManager fm, Resources res) {
        super(fm);
        titles = res.getStringArray(R.array.login_or_register);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return LoginFragment.newInstance(null);
        } else {
            return RegisterFragment.newInstance(null);
        }

    }

    @Override
    public int getCount() {
        return AMOUNT_OF_SECTIONS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
