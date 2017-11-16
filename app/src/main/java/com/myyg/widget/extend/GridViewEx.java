package com.myyg.widget.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by JOHN on 2016/5/21.
 */
public class GridViewEx extends GridView {
    public GridViewEx(Context context) {
        super(context);
    }

    public GridViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
