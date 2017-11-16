package com.myyg.widget.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * Created by JOHN on 2015/11/28.
 */
public class AnimatedView extends View {

    private int target;

    public AnimatedView(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public float getXFactor() {
        return getX() / target;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setXFactor(float xFactor) {
        setX(target * xFactor);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}