package news.intelli.intellinews;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import news.intelli.intellinews.adapter.pager.NewsFragmentPagerAdapter;
import news.intelli.intellinews.fragment.dialog.DeleteDialogFragment;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.presenter.KeyWordPresenter;
import news.intelli.intellinews.mvp.view.KeyWordView;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import timber.log.Timber;

/**
 * Created by llefoulon on 24/10/2016.
 */
//final view of http://saulmm.github.io/mastering-coordinator
//https://mzgreen.github.io/2015/06/23/How-to-hideshow-Toolbar-when-list-is-scrolling(part3)/
public class NewsActivity extends AppCompatActivity implements KeyWordView,
        ViewPager.OnPageChangeListener,
        Toolbar.OnMenuItemClickListener {

    public static final String TAG = NewsActivity.class.getName();
    private final static KeyWordPresenter presenter = new KeyWordPresenter();

    @BindView(R.id.activity_news_viewPager)
    protected ViewPager viewPager;

    @BindView(R.id.activity_news_toolbar)
    protected Toolbar toolbar;

    protected boolean selectedTabIsRegistered = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        toolbar.setOnMenuItemClickListener(this);
        viewPager.addOnPageChangeListener(this);
        TabLayout tabLayout = ButterKnife.findById(this, R.id.activity_news_tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        if (!IntelliNewsApplication.getInstance().isTablet())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //http://www.tristanwaddington.com/2013/03/styling-the-android-action-bar-with-a-custom-font/
        //Toolbar toolbar = (Toolbar) findViewById(R.id.activity_news_toolbar);
        //toolbar.setTitle("Coucou");
        presenter.attachView(this);
        presenter.fetchEveryKeywords();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_element, menu);
        return true;
    }*/

    @Override
    public void onDestroy() {
        presenter.removeView(this);
        viewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    public Context getMVPViewContext() {
        return this;
    }

    @Override
    public void onStartProcessing() {
    }

    @Override
    public void onFetchingKeywordsError(Throwable e) {
    }

    @Override
    public void onFetchingKeywordsSucceed(List<Keyword> keywords) {
        if (keywords == null) {
            onFetchingKeywordsError(new ThrowableWithErrorCode(ThrowableWithErrorCode.GENERIC_ERROR));
            return;
        }

        NewsFragmentPagerAdapter pagerAdapter = new NewsFragmentPagerAdapter(getSupportFragmentManager());
        Keyword keyword;
        for (int i = 0, size = keywords.size(); i < size; ++i) {
            keyword = keywords.get(i);
            pagerAdapter.addKeyWord(keyword); //IntelliNewsContentProvider.KEYWORD_CONTENT_URI + "/keyword.getValue()"

        }
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();
        selectedTabIsRegistered = keywords.get(0).getIsRegistered();
        setToolbarItem(selectedTabIsRegistered);
        //viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private void setToolbarItem(boolean isRegistered) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(!isRegistered ? R.menu.unregister_element : R.menu.register_element);
    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("onPageSelected %d", position);
        //ActivityCompat.invalidateOptionsMenu(this);
        NewsFragmentPagerAdapter adapter = (NewsFragmentPagerAdapter) viewPager.getAdapter();
        boolean isRegistered = adapter.getIsRegistered(position);
        if (isRegistered != selectedTabIsRegistered) {
            setToolbarItem(isRegistered);
        }

        selectedTabIsRegistered = isRegistered;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item == null)
            return false;

        switch (item.getItemId()) {
            case R.id.menu_register: {
                NewsFragmentPagerAdapter adapter = (NewsFragmentPagerAdapter) viewPager.getAdapter();
                presenter.unRegisterKeyword(adapter.getKeyword(viewPager.getCurrentItem()));
            }
            break;
            case R.id.menu_unregister: {
                NewsFragmentPagerAdapter adapter = (NewsFragmentPagerAdapter) viewPager.getAdapter();
                presenter.registerKeyword(adapter.getKeyword(viewPager.getCurrentItem()));
            }
            break;
            case R.id.menu_delete: {
                FragmentManager fm = getSupportFragmentManager();
                DeleteDialogFragment editNameDialogFragment = DeleteDialogFragment.newInstance("Some Title");
                editNameDialogFragment.show(fm, "delete_content_dialog");

            }
            break;
        }
        return false;
    }
}
