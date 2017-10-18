package news.intelli.intellinews.mvp.model;

import android.support.annotation.DrawableRes;

import java.text.ParseException;
import java.util.Date;

import news.intelli.intellinews.R;
import timber.log.Timber;


/**
 * Created by llefoulon on 06/11/2016.
 */

public class GuardianArticle extends News{

    public static final String SOURCE = "Guardian";

    class GuardianArticleFields {
        protected String shortUrl;
        protected String thumbnailUrl;
    }

    protected String id;
    protected String sectionId;
    protected String webTitle;
    protected String webUrl;
    protected String apiUrl;
    protected String webPublicationDate;
    protected GuardianArticleFields fields;

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return webTitle;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public String getSource() {
        return SOURCE;
    }

    @DrawableRes
    @Override
    public int getDefaultThumbnailDrawableID() {
        return R.mipmap.guardian;
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
        if(webPublicationDate == null)
            return -1;

        try {
            Date d = News.SIMPLE_DATE_FORMAT.parse(webPublicationDate);
            return d.getTime();
        } catch (Exception e) {
            Timber.e(e.toString());
            return -1;
        }
    }

    @Override
    public String getURL() {
        return apiUrl;
    }

    @Override
    public String[] getKeywords() {
        return null;
    }
}
