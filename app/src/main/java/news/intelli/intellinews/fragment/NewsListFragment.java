package news.intelli.intellinews.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import news.intelli.intellinews.R;
import news.intelli.intellinews.adapter.recyclerview.NewsRecyclerAdapter;
import news.intelli.intellinews.mvp.model.Keyword;
import news.intelli.intellinews.mvp.model.News;
import news.intelli.intellinews.mvp.model.WikipediaDefinition;
import news.intelli.intellinews.mvp.presenter.DefinitionPresenter;
import news.intelli.intellinews.mvp.presenter.NewsPresenter;
import news.intelli.intellinews.mvp.view.DefinitionView;
import news.intelli.intellinews.mvp.view.NewsView;
import news.intelli.intellinews.utils.animations.TransitionDrawableAnimationUtils;
import news.intelli.intellinews.utils.errors.ThrowableWithErrorCode;
import timber.log.Timber;

/**
 * Created by llefoulon on 04/11/2016.
 */

public class NewsListFragment extends Fragment implements NewsView, DefinitionView, View.OnClickListener {
    public static final String KEYWORD = "keyword";

    private NewsPresenter newsPresenter = null;
    private DefinitionPresenter definitionsPresenter = null;

    @BindView(R.id.fragment_news_list_recyclerview)
    protected RecyclerView recyclerView;

    @BindView(R.id.fragment_news_list_loading_container)
    protected View loadingContainer;

    @BindView(R.id.layout_loading_imageview)
    protected ImageView loadingImageView;

    @BindView(R.id.layout_loading_progressbar)
    protected ProgressBar loadingProgressBar;

    @BindView(R.id.layout_loading_textview)
    protected TextView loadingTextView;

    @BindView(R.id.layout_loading_reload_button)
    protected Button reloadButton;

    @BindView(R.id.layout_loading_header_textview)
    protected TextView definitionTextView;

    private TransitionDrawable tDrawable = null;

    private Unbinder unbinder = null;

    public static NewsListFragment newsInstance(Keyword keyword) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEYWORD, keyword);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsPresenter = new NewsPresenter(null);
        definitionsPresenter = new DefinitionPresenter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        ButterKnife.findById(view, R.id.layout_loading_reload_button).setOnClickListener(this);
        GridLayoutManager lLayoutManager = new GridLayoutManager(recyclerView.getContext(), getResources().getInteger(R.integer.news_span_count));

        recyclerView.setLayoutManager(lLayoutManager);

        newsPresenter.attachView(this);
        definitionsPresenter.attachView(this);

        fetchData();

        return view;
    }

    private void fetchData() {
        Bundle args = getArguments();
        Keyword keyWord = args.getParcelable(KEYWORD);
        String value = keyWord != null ? keyWord.getValue() : null;

        definitionsPresenter.fetchDefinition(value, false);
        newsPresenter.fetchNews(value);
    }


    @Override
    public void onDestroyView() {

        newsPresenter.removeView(this);
        definitionsPresenter.removeView(this);
        if (unbinder != null)
            unbinder.unbind();

        tDrawable = null;

        super.onDestroyView();
    }

    @Override
    public Context getMVPViewContext() {
        return getActivity();
    }

    @Override
    public void onStartProcessing() {
        if (tDrawable != null)
            tDrawable.reverseTransition(TransitionDrawableAnimationUtils.TRANSITION_SPEED);

        loadingTextView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        reloadButton.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetNewsSucceed(List<News> news) {

        loadingContainer.setVisibility(View.GONE);

        NewsRecyclerAdapter recyclerAdapter = new NewsRecyclerAdapter(getActivity(), news);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onGetNewsFailed(Throwable e) {
        int errorCode = ThrowableWithErrorCode.GENERIC_ERROR;
        if (e instanceof ThrowableWithErrorCode)
            errorCode = ((ThrowableWithErrorCode) e).getErrorCode();


        if (tDrawable == null)
            tDrawable = TransitionDrawableAnimationUtils.applyTransition(getActivity(), loadingImageView, R.drawable.transition_magic_link);

        tDrawable.startTransition(TransitionDrawableAnimationUtils.TRANSITION_SPEED);

        recyclerView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);
        reloadButton.setVisibility(View.VISIBLE);

        if (errorCode == ThrowableWithErrorCode.GENERIC_ERROR) {
            loadingTextView.setText(getString(R.string.error_generic));
        } else if (errorCode == ThrowableWithErrorCode.NO_NETWORK) {
            loadingTextView.setText(getString(R.string.error_no_network));
        }

        loadingTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        if (view.getId() == R.id.layout_loading_reload_button) {
            fetchData();
        }
    }

    @Override
    public void onFetchDefinitionSucceed(List<WikipediaDefinition> definitions) {
        //Timber.d("onFetchDefinitionSucceed %d",definitions != null ? definitions.size() : 0);
        int size = definitions != null ? definitions.size() : 0;
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(size);
        WikipediaDefinition def = definitions.get(randomNumber);
        //load def during news loading
        String title = def.getTitle();
        int titleLen = title.length();
        SpannableString sString = new SpannableString(String.format("%s\n%s",title,def.getDesc()));
        sString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0,titleLen,0);
        sString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(),R.color.colorPrimary)), 0, titleLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        definitionTextView.setText(sString);
        //end

    }

    @Override
    public void onFetchDefinitionFailed(Throwable e) {
        Timber.w("onFetchDefinitionFailed %s", e.toString());

    }
}
