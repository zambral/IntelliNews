package news.intelli.intellinews.utils.animations;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;

/**
 * Created by llefoulon on 31/10/2016.
 */

public class ExpandAnimation extends Animation {

    private WeakReference<View> wView;
    private int targetHeight = 0;

    public ExpandAnimation(View v,int height) {
        wView = new WeakReference<>(v);


        if (v != null) {
            targetHeight = height;
            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        View view = wView.get();
        if (view == null)
            return;

        view.getLayoutParams().height = interpolatedTime == 1
                ? ViewGroup.LayoutParams.WRAP_CONTENT
                : (int) (targetHeight * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
