package com.myyg.widget.xrefreshview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andview.refreshview.callback.IHeaderCallBack;
import com.myyg.R;
import com.myyg.utils.MyLog;

/**
 * Created by JOHN on 2016/6/18.
 */
public class MyygRefreshHeader extends LinearLayout implements IHeaderCallBack {
    private final static String TAG = MyygRefreshHeader.class.getSimpleName();

    private Context mContext;
    private ImageView iv_default;
    private ImageView iv_refresh;
    private AnimationDrawable anim;
    private int[] arrayAnim = new int[]{R.mipmap.ic_anim_0017, R.mipmap.ic_anim_0018, R.mipmap.ic_anim_0019, R.mipmap.ic_anim_0020,
            R.mipmap.ic_anim_0021, R.mipmap.ic_anim_0022, R.mipmap.ic_anim_0023, R.mipmap.ic_anim_0024, R.mipmap.ic_anim_0025, R.mipmap.ic_anim_0026};

    public MyygRefreshHeader(Context context) {
        super(context);
        this.mContext = context;
        this.initView();
    }

    private void initView() {
        LayoutInflater.from(this.mContext).inflate(R.layout.view_myyg_refresh_header, this);
        this.iv_default = (ImageView) this.findViewById(R.id.iv_default);
        this.iv_refresh = (ImageView) this.findViewById(R.id.iv_refresh);
        this.anim = (AnimationDrawable) this.iv_refresh.getBackground();
    }

    @Override
    public void onStateNormal() {
        this.iv_default.setVisibility(VISIBLE);
        this.iv_refresh.setVisibility(GONE);
    }

    @Override
    public void onStateReady() {

    }

    @Override
    public void onStateRefreshing() {
        MyLog.i(TAG, "刷新中");
        this.iv_default.setVisibility(GONE);
        this.iv_refresh.setVisibility(VISIBLE);
        this.anim.start();
    }

    @Override
    public void onStateFinish() {
        MyLog.i(TAG, "刷新结束");
        this.anim.stop();
    }

    @Override
    public void onHeaderMove(double offset, int offsetY, int deltaY) {
        try {
            int index = (int) (((float) offsetY / (float) this.getHeaderHeight()) * 100) / 10;
            if (index < 0) {
                return;
            }
            if (index > 9) {
                index = 9;
            }
            this.iv_default.setImageResource(arrayAnim[index]);
        } catch (Exception ex) {
            MyLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void setRefreshTime(long lastRefreshTime) {

    }

    @Override
    public void hide() {
        //this.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    @Override
    public int getHeaderHeight() {
        return getMeasuredHeight();
    }
}
