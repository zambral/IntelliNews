package news.intelli.intellinews.mvp.model;

import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import news.intelli.intellinews.R;
import timber.log.Timber;

/**
 * Created by llefoulon on 06/11/2016.
 */

public class NYTimesArticle extends News {

    public static final String SOURCE = "New York Times";

    class NYTimesKeyWords {
        protected String rank,is_major,name,value;
    }

    class NYTimesMedia {
        protected int width,height;
        protected String url;
        protected String type;
    }

    class NYTimesArticleHeadline {
        protected String main,print_headline;
    }

    protected String web_url;
    protected String snippet;
    protected NYTimesArticleHeadline headline;
    //protected String lead_paragraph;
    protected ArrayList<NYTimesKeyWords> keywords;
    protected ArrayList<NYTimesMedia> multimedia;
    //protected String section_name;
    protected String pub_date;
    //rotected String document_type;//values : article
    @SerializedName("_id")
    protected String id;

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return headline != null ? headline.main : null;
    }

    @Override
    public String getDesc() {
        return snippet;
    }

    @Override
    public String getSource() {
        return SOURCE;
    }

    @DrawableRes
    @Override
    public int getDefaultThumbnailDrawableID() {
        return R.mipmap.nyt_logo_black;
    }

    @Override
    public String getThumbnailImagePath() {
        return null;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public long getTimestamp() {
        //Example : 2016-11-16T00:00:00Z
        if(pub_date == null)
            return -1;

        try {
            Date d = SIMPLE_DATE_FORMAT.parse(pub_date);
            return d.getTime();
        } catch (Exception e) {
            Timber.e(e.toString());
            return -1;
        }
    }

    @Override
    public String getURL() {
        return web_url;
    }

    @Override
    public String[] getKeywords() {
        if(keywords != null) {
            int size = keywords.size();
            String[] kwords = new String[size];
            for(int i = 0;i < size;++i) {
                kwords[i] = keywords.get(i).value;
            }

            return kwords;
        }

        return null;
    }


}
