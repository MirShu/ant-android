package com.myyg.adapter.recycler;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.common.ImageLoaderEx;
import com.myyg.model.CountdownBaseModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by JOHN on 2016/5/21.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolder";

    private final SparseArray<View> listView;

    private View mConvertView;

    private CountdownBaseModel model;

    private CountdownView mCountdownView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.listView = new SparseArray<View>();
        this.mConvertView = itemView;
        this.mCountdownView = this.getView(R.id.cv_goods);
        itemView.setTag(this);
    }

    public <T extends View> T getView(int viewId) {
        View view = listView.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            listView.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public RecyclerViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public RecyclerViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public RecyclerViewHolder setImageUrl(int viewId, String url) {
        ImageView view = this.getView(viewId);
        ImageLoader.getInstance().displayImage(url, view, ImageLoaderEx.getDisplayImageOptions());
        return this;
    }

    /**
     * @return
     */
    public CountdownBaseModel getModel() {
        return model;
    }

    /**
     * @param model
     */
    public void setModel(CountdownBaseModel model) {
        this.model = model;
        if (this.model.getCountdown() > 0) {
            this.refreshTime(System.currentTimeMillis());
            return;
        }
        if (null != this.mConvertView) {
            this.mCountdownView.allShowZero();
        }
    }

    /**
     * @param currentTimeMillis
     */
    public void refreshTime(long currentTimeMillis) {
        if (null == this.model || this.model.getCountdown() <= 0) {
            return;
        }
        if (null != this.mCountdownView) {
            this.mCountdownView.updateShow(this.model.getEndTime() - currentTimeMillis);
        }
    }
}
