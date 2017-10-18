package news.intelli.intellinews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import news.intelli.intellinews.adapter.pager.LoginOrRegisterPagerAdapter;
import news.intelli.intellinews.utils.animations.CollapseAnimation;
import news.intelli.intellinews.utils.animations.ExpandAnimation;

/**
 * Created by llefoulon on 24/10/2016.
 */
//https://gist.github.com/iPaulPro/1468510f046cb10c51ea
//https://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout
public class LoginActivity extends AppCompatActivity {

    public static final String ELEMENT_HAS_FOCUS = "element_has_focus";
    private static final String HEADER_INITIAL_HEIGHT = "initialHeaderHeight";
    private static final String HEADER_VISIBILITY = "header-visibility";
    public static final String IS_PROCESSING = "background-processing";

    @BindView(R.id.activity_login_header)
    protected View header;

    @BindView(R.id.activity_login_header_imageview)
    protected ImageView headerImageView;

    @BindView(R.id.activity_login_header_progress_bar)
    protected CircularProgressBar progressBar;

    private int initialHeaderHeight = 0;
    private BroadcastReceiver receiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_login_viewpager);
        viewPager.setAdapter(new LoginOrRegisterPagerAdapter(getSupportFragmentManager(), getResources()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_login_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        IntentFilter intentFilter = new IntentFilter();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ELEMENT_HAS_FOCUS.equalsIgnoreCase(action)) {
                    setHeaderVisible(!intent.getBooleanExtra(ELEMENT_HAS_FOCUS, false));
                } else if(IS_PROCESSING.equalsIgnoreCase(action)) {
                    updateHeader(intent.getBooleanExtra(IS_PROCESSING,false));
                }
            }
        };

        if(!IntelliNewsApplication.getInstance().isTablet()) {
            intentFilter.addAction(ELEMENT_HAS_FOCUS);
        }
        intentFilter.addAction(IS_PROCESSING);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);


        if(savedInstanceState != null) {
            initialHeaderHeight = savedInstanceState.getInt(HEADER_INITIAL_HEIGHT,0);
            boolean isVisible = savedInstanceState.getBoolean(HEADER_VISIBILITY,true);
            if(!isVisible)
                header.setVisibility(View.GONE);
        }
    }

    protected void updateHeader(boolean isProcessing){
        if(isProcessing) {
            headerImageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            headerImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }


    public void setHeaderVisible(final boolean isVisible) {
        Animation a = null;
        header.clearAnimation();
        if (!isVisible) {
            if(header.getVisibility() == View.VISIBLE) {
                initialHeaderHeight = Math.max(initialHeaderHeight,header.getMeasuredHeight());
                a = new CollapseAnimation(header);
            }
        } else {
            if(header.getVisibility() != View.VISIBLE)
                a = new ExpandAnimation(header,initialHeaderHeight);
        }

        if(a != null) {
            a.setDuration(400);
            header.startAnimation(a);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(HEADER_INITIAL_HEIGHT,initialHeaderHeight);
        outState.putBoolean(HEADER_VISIBILITY,header.getVisibility() == View.VISIBLE);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onDestroy();
    }
}
