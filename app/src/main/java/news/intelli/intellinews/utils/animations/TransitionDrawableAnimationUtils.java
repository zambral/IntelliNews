package news.intelli.intellinews.utils.animations;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

/**
 * Created by llefoulon on 11/11/2016.
 */

public final class TransitionDrawableAnimationUtils {
    public static final int TRANSITION_SPEED = 300;

    private TransitionDrawableAnimationUtils(){}

    public static TransitionDrawable applyTransition(Context context, ImageView imageview, @DrawableRes int transitionID){
        TransitionDrawable transitionDrawable = (TransitionDrawable) ContextCompat.getDrawable(context, transitionID);
        transitionDrawable.setCrossFadeEnabled(true);
        imageview.setImageDrawable(transitionDrawable);

        return transitionDrawable;

    }
}
