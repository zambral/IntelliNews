package news.intelli.intellinews.utils;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by llefoulon on 20/11/2016.
 */

public class ReleaseTree extends Timber.Tree {

    @Override
    protected boolean isLoggable(String tag,int priority) {
        if(priority == Log.VERBOSE ||
                priority == Log.DEBUG ||
                priority == Log.INFO)
            return false;

        return true;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(!isLoggable(tag,priority))
            return;

        if(priority == Log.ERROR && t!= null) {
            //Crashlytics.log(e);
        }

        Log.println(priority,tag,message);


    }
}
