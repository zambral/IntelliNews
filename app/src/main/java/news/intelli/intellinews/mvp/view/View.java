package news.intelli.intellinews.mvp.view;

import android.content.Context;

/**
 * Created by llefoulon on 25/10/2016.
 */

public interface View {
    Context getMVPViewContext();

    void onStartProcessing();
}
