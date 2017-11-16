package com.myyg.banner.holder;

import android.content.Context;
import android.view.View;

/**
 * Created by JOHN on 2016/5/24.
 */
public interface BannerHolder<T> {
    View create(Context context);

    void modify(Context context, int position, T data);
}