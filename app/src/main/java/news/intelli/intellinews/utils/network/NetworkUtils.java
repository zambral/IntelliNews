package news.intelli.intellinews.utils.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import news.intelli.intellinews.IntelliNewsApplication;

/**
 * Created by llefoulon on 11/11/2016.
 */

public class NetworkUtils {

    // Define the list of accepted constants and declare the NavigationMode annotation
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_WIFI, TYPE_MOBILE, TYPE_NOT_CONNECTED})
    public @interface ConnectivityType {}

    public static final String NETWORK_UPDATED = "network-updated";

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

    private static NetworkUtils instance = null;
    @ConnectivityType
    private int lastActiveNetworkType = TYPE_NOT_CONNECTED;

    private NetworkUtils() {}


    public static synchronized NetworkUtils getInstance() {
        if (instance == null) {
            instance = new NetworkUtils();
        }

        return instance;
    }

    private static void sendUpdatedNetworkState(Context context,@ConnectivityType int state){
        Intent intent = new Intent(NETWORK_UPDATED);
        intent.putExtra(NETWORK_UPDATED,state);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    //TODO is synchronized really necessary
    public synchronized void updateConnectivityStatus(Context context) {
        if (context == null) {
            lastActiveNetworkType = TYPE_NOT_CONNECTED;
            sendUpdatedNetworkState(IntelliNewsApplication.getInstanceContext(),lastActiveNetworkType);
            return;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    NetworkInfo.DetailedState detailedState = activeNetwork.getDetailedState();
                    lastActiveNetworkType = (detailedState != null &&
                            detailedState.compareTo(NetworkInfo.DetailedState.CONNECTED) == 0)
                            ? TYPE_WIFI : TYPE_NOT_CONNECTED;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    lastActiveNetworkType = TYPE_MOBILE;
                    break;
                default:
                    lastActiveNetworkType = TYPE_NOT_CONNECTED;
                    break;
            }
        } else {
            lastActiveNetworkType = TYPE_NOT_CONNECTED;
        }

        sendUpdatedNetworkState(context,lastActiveNetworkType);
    }

    @ConnectivityType
    public int getActiveNetworkState() {
        return lastActiveNetworkType;
    }

    public String getActiveNetworkStateString() {
        switch (lastActiveNetworkType) {
            case TYPE_MOBILE:
                return "3g";
            case TYPE_NOT_CONNECTED:
                return "offline";
            case TYPE_WIFI:
                return "wifi";
            default:
                return null;
        }
    }


}
