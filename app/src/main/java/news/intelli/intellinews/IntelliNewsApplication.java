package news.intelli.intellinews;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.lang.ref.WeakReference;

import news.intelli.intellinews.utils.GMTController;
import news.intelli.intellinews.utils.ReleaseTree;
import news.intelli.intellinews.utils.network.NetworkUtils;
import rx.plugins.RxJavaHooks;
import timber.log.Timber;

/**
 * Created by llefoulon on 29/10/2016.
 */

public class IntelliNewsApplication extends Application {

    public static final String SHARED_PREFERENCES = "intellinews_preferences";

    private static WeakReference<IntelliNewsApplication> wInstance = new WeakReference<>(null);

    private boolean isTablet = false;

    @Override
    public void onCreate(){
        super.onCreate();


        RxJavaHooks.enableAssemblyTracking();
        wInstance = new WeakReference<>(this);
        Resources res = getResources();
        isTablet = res.getBoolean(R.bool.is_tablet);

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }

        NetworkUtils.getInstance().updateConnectivityStatus(this);
        GMTController.init(this);
    }

    public static IntelliNewsApplication getInstance(){
        return wInstance.get();
    }

    public static Context getInstanceContext(){
        IntelliNewsApplication app = wInstance.get();
        return (app != null) ? app.getApplicationContext() : null;
    }

    public boolean isTablet() {
        return isTablet;
    }
}
