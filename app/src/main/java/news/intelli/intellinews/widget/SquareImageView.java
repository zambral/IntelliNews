package news.intelli.intellinews.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import news.intelli.intellinews.R;

/**
 * Created by llefoulon on 05/11/2016.
 */

public class SquareImageView extends AppCompatImageView {
    private int referenceSize = 1;//1 -> width, 2 -> height

    public SquareImageView(Context context) {
        super(context);
        init(context,null,0);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if(isInEditMode())
            return;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareImageView, defStyleAttr, 0);
        referenceSize = a.getInt(R.styleable.SquareImageView_reference_size,1);
        a.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(referenceSize == 1)
            super.onMeasure(widthMeasureSpec,widthMeasureSpec);
        else
            super.onMeasure(heightMeasureSpec,heightMeasureSpec);
    }
}
