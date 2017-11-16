package com.myyg.widget.loading;

import android.animation.*;
import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by JOHN on 2015/11/28.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorPlayer extends AnimatorListenerAdapter {

    private boolean interrupted = false;
    private Animator[] animators;

    public AnimatorPlayer(Animator[] animators) {
        this.animators = animators;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!interrupted) animate();
    }

    public void play() {
        animate();
    }

    public void stop() {
        interrupted = true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animate() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.addListener(this);
        set.start();
    }
}
