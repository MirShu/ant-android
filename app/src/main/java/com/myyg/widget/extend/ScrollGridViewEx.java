package com.myyg.widget.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by shiyuankao on 2016/6/16.
 */
public class ScrollGridViewEx extends GridView {
    public ScrollGridViewEx(Context context) {
        super(context);
    }

    public ScrollGridViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollGridViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}