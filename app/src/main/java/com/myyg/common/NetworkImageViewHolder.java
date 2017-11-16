package com.myyg.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.myyg.R;
import com.myyg.banner.holder.BannerHolder;
import com.myyg.utils.BitmapHelper;
import com.myyg.utils.CommonHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by JOHN on 2016/5/24.
 */
public class NetworkImageViewHolder implements BannerHolder<String> {
    private ImageView imageView;

    @Override
    public View create(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void modify(Context context, int position, String data) {
        ImageLoader.getInstance().displayImage(data, imageView, ImageLoaderEx.getDefaultDisplayImageOptions());
    }
}
