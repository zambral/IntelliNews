package news.intelli.intellinews.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by llefoulon on 11/11/2016.
 */

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtils.getInstance().updateConnectivityStatus(context);
    }
}
