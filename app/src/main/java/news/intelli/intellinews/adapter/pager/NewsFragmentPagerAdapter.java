package news.intelli.intellinews.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;

import java.util.ArrayList;

import news.intelli.intellinews.fragment.NewsListFragment;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.utils.CustomTypefaceSpan;

/**
 * Created by llefoulon on 04/11/2016.
 */

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Keyword> keywords = null;

    public NewsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        keywords = new ArrayList<>(5);
    }

    @Override
    public Fragment getItem(int position) {
        return NewsListFragment.newsInstance(keywords.get(position));
    }

    public void addKeyWord(Keyword keyword) {
        keywords.add(keyword);
    }

    @Override
    public int getCount() {
        return keywords.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*SpannableString tmp;
        String title = keywords.get(position).getValue();
        tmp = new SpannableString(title);
        tmp.setSpan(new CustomTypefaceSpan("", "font/SourceSansPro-Italic.ttf"), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tmp;*/
        return keywords.get(position).getValue();
    }

    public boolean getIsRegistered(int position) {
        return keywords.get(position).getIsRegistered();
    }

    public Keyword getKeyword(int currentItem) {
        return keywords.get(currentItem);
    }
}
