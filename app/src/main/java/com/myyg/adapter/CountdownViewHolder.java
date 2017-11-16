package com.myyg.adapter;

import android.view.View;

import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.model.CountdownBaseModel;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by JOHN on 2016/6/23.
 */
public class CountdownViewHolder extends RecyclerViewHolder {
    private CountdownBaseModel model;
    private CountdownView mCountdownView;

    /**
     * @param itemView
     */
    public CountdownViewHolder(View itemView) {
        super(itemView);
        this.mCountdownView = this.getView(R.id.cv_goods);
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
        } else {
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
