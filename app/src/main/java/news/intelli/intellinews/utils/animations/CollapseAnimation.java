package news.intelli.intellinews.utils.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;

/**
 * Created by llefoulon on 31/10/2016.
 */

public class CollapseAnimation extends Animation {

    private WeakReference<View> wView;
    private int initialHeight;

    public CollapseAnimation(View v) {
        wView = new WeakReference<>(v);
        initialHeight = v != null ? v.getMeasuredHeight() : 0;
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        View view = wView.get();
        if (view == null)
            return;

        if (interpolatedTime == 1) {
            view.setVisibility(View.GONE);
        } else {
            view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
            view.requestLayout();
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
