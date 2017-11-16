package com.myyg.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;

import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.model.CountdownBaseModel;
import com.myyg.utils.MyLog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JOHN on 2016/6/23.
 */
public abstract class CountdownAdapter<T extends CountdownBaseModel> extends RecyclerAdapter<T> {
    private static final String TAG = CountdownAdapter.class.getSimpleName();
    private final SparseArray<RecyclerViewHolder> listCountdownHolder;
    private Handler mHandler = new Handler();
    private Timer mTimer;
    private boolean isCancel = true;
    private OnCountdownListener listener;

    public CountdownAdapter(Context context, List<T> listData, int itemLayoutId) {
        super(context, listData, itemLayoutId);
        this.listCountdownHolder = new SparseArray<>();
        this.startRefreshTime();
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position, boolean isItem) {
        super.onBindViewHolder(holder, position, isItem);
        CountdownBaseModel model = listData.get(position);
        holder.setModel(model);
        if (model.getCountdown() > 0) {
            synchronized (listCountdownHolder) {
                listCountdownHolder.put(model.getId(), holder);
            }
        }
    }

    @Override
    public void onViewRecycled(RecyclerViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof CountdownViewHolder) {
            CountdownBaseModel current = holder.getModel();
            if (null != current && current.getCountdown() > 0) {
                this.listCountdownHolder.remove(current.getId());
            }
        }
    }

    /**
     * @param listener
     */
    public void setCountdownListener(OnCountdownListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    public void startRefreshTime() {
        if (!isCancel) return;
        if (null != this.mTimer) {
            this.mTimer.cancel();
        }
        this.isCancel = false;
        this.mTimer = new Timer();
        this.mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(mRefreshTimeRunnable);
            }
        }, 0, 10);
    }

    /**
     *
     */
    public void cancelRefreshTime() {
        this.isCancel = true;
        if (null != this.mTimer) {
            this.mTimer.cancel();
        }
        this.mHandler.removeCallbacks(mRefreshTimeRunnable);
    }

    /**
     *
     */
    private Runnable mRefreshTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (listCountdownHolder.size() == 0) {
                return;
            }
            synchronized (listCountdownHolder) {
                try {
                    long currentTime = System.currentTimeMillis();
                    int key;
                    for (int i = 0; i < listCountdownHolder.size(); i++) {
                        key = listCountdownHolder.keyAt(i);
                        RecyclerViewHolder viewHolder = listCountdownHolder.get(key);
                        if (currentTime >= viewHolder.getModel().getEndTime()) {
                            // 倒计时结束
                            viewHolder.getModel().setCountdown(0);
                            listCountdownHolder.remove(key);
                            notifyDataSetChanged();
                            if (listener != null) {
                                listener.onEnd(viewHolder);
                            }
                        } else {
                            viewHolder.refreshTime(currentTime);
                        }
                    }
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                }
            }
        }
    };

    public interface OnCountdownListener {
        void onEnd(RecyclerViewHolder viewHolder);
    }
}
