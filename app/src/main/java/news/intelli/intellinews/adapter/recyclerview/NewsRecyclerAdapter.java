package news.intelli.intellinews.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import news.intelli.intellinews.IntelliNewsApplication;
import news.intelli.intellinews.R;
import news.intelli.intellinews.adapter.recyclerview.viewholder.NewsHolder;
import news.intelli.intellinews.mvp.model.News;

/**
 * Created by llefoulon on 04/11/2016.
 */

//https://dzone.com/articles/design-patterns-visitor
//http://www.journaldev.com/1769/visitor-design-pattern-java
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsHolder> {
    private LayoutInflater inflater;

    private List<News> newsList = null;
    private int newsSize = 0;

    public NewsRecyclerAdapter(Context context,@NonNull List<News> news) {
        //values = strings;
        inflater = LayoutInflater.from(context);
        newsList = news;
        newsSize = news != null ? news.size() : 0;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsHolder(inflater.inflate(R.layout.cell_news,parent,false));
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {
        News news = newsList.get(position);
        holder.visit(news);
    }

    @Override
    public int getItemCount() {
        return newsSize;
    }
}
