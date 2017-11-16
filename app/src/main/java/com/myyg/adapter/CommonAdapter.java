package com.myyg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by shiyuankao on 2015-08-26.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    private static final String TAG = "CommonAdapter";
    protected LayoutInflater inflater;
    protected Context context;
    protected List<T> listData;
    protected final int itemLayoutId;

    public CommonAdapter(Context context, List<T> listData, int itemLayoutId) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listData = listData;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    /**
     * @param helper
     * @param item
     * @param position
     */
    public abstract void convert(ViewHolder helper, T item, int position);

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(context, convertView, parent, itemLayoutId, position);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getDataSource() {
        return listData;
    }
}