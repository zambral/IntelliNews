package news.intelli.intellinews.adapter.recyclerview.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import news.intelli.intellinews.R;
import news.intelli.intellinews.interfaces.NewsVisitor;
import news.intelli.intellinews.mvp.model.News;

/**
 * Created by llefoulon on 04/11/2016.
 */

public class NewsHolder extends RecyclerView.ViewHolder implements NewsVisitor {

    @BindView(R.id.cell_news_title_textview)
    protected TextView titleTextView;

    @BindView(R.id.cell_news_desc_texview)
    protected TextView descTextView;

    @BindView(R.id.cell_news_imageview)
    protected ImageView thumbnail;

    public NewsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void visit(News news) {
        titleTextView.setText(news.getTitle());
        String s = news.getDesc();
        descTextView.setText(s != null ? Html.fromHtml(news.getDesc()): null);
        thumbnail.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),news.getDefaultThumbnailDrawableID()));
        //holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
}
