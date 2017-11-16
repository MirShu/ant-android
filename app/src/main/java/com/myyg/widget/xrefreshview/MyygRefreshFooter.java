package com.myyg.widget.xrefreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.callback.IFooterCallBack;
import com.myyg.R;

/**
 * Created by JOHN on 2016/8/2.
 */
public class MyygRefreshFooter extends LinearLayout implements IFooterCallBack {
    private Context mContext;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;
    private TextView mClickView;
    private boolean showing = true;

    public MyygRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public MyygRefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @Override
    public void callWhenNotAutoLoadMore(final XRefreshView.XRefreshViewListener listener) {
        mClickView.setText(com.andview.refreshview.R.string.xrefreshview_footer_hint_click);
        mClickView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLoadMore(false);
                onStateRefreshing();
            }
        });
    }

    @Override
    public void onStateReady() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void onStateRefreshing() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mClickView.setVisibility(View.GONE);
        show(true);
    }

    @Override
    public void onStateFinish(boolean hideFooter) {
        if (hideFooter) {
            mHintView.setText(com.andview.refreshview.R.string.xrefreshview_footer_hint_normal);
        } else {
            //处理数据加载失败时ui显示的逻辑，也可以不处理，看自己的需求
            mHintView.setText(com.andview.refreshview.R.string.xrefreshview_footer_hint_fail);
        }
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void onStateComplete() {
        mHintView.setText(com.andview.refreshview.R.string.xrefreshview_footer_hint_complete);
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void show(final boolean show) {
        post(() -> {
            showing = show;
            LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
            lp.height = show ? LayoutParams.WRAP_CONTENT : 0;
            mContentView.setLayoutParams(lp);
        });

    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    private void initView(Context context) {
        mContext = context;
        ViewGroup moreView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_myyg_refresh_footer, this);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(com.andview.refreshview.R.id.xrefreshview_footer_content);
        mProgressBar = moreView.findViewById(com.andview.refreshview.R.id.xrefreshview_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(com.andview.refreshview.R.id.xrefreshview_footer_hint_textview);
        mClickView = (TextView) moreView.findViewById(com.andview.refreshview.R.id.xrefreshview_footer_click_textview);
        mHintView.setVisibility(GONE);
        mClickView.setVisibility(GONE);
    }

    @Override
    public int getFooterHeight() {
        return getMeasuredHeight();
    }
}