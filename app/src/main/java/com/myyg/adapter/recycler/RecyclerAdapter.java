package com.myyg.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.myyg.R;
import com.myyg.adapter.ViewHolder;

import java.util.List;

/**
 * Created by JOHN on 2016/5/21.
 */
public abstract class RecyclerAdapter<T> extends BaseRecyclerAdapter<RecyclerViewHolder> {

    protected Context context;

    protected LayoutInflater inflater;

    protected List<T> listData;

    protected final int itemLayoutId;

    private OnItemClickListener itemClickListener;

    private OnItemLongClickListener itemLongClickListener;

    public RecyclerAdapter(Context context, List<T> listData, int itemLayoutId) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listData = listData;
        this.itemLayoutId = itemLayoutId;
    }

      @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = this.inflater.inflate(itemLayoutId, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    /**
     * @param position
     * @return
     */
    public T getItem(int position) {
        return listData.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position, boolean isItem) {
        convert(holder, getItem(position), position);
        holder.itemView.setOnClickListener((v) -> {
            if (itemClickListener != null) {
                itemClickListener.onClick(v, position);
            }
        });
        holder.itemView.setOnLongClickListener((v) -> {
            if (itemLongClickListener != null) {
                itemLongClickListener.onLongClick(v, position);
            }
            return false;
        });
    }

    public void insert(T object, int position) {
        insert(listData, object, position);
    }

    /**
     * @param helper
     * @param item
     * @param position
     */
    public abstract void convert(RecyclerViewHolder helper, T item, int position);

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return listData.size();
    }

    @Override
    public RecyclerViewHolder getViewHolder(View view) {
        return new RecyclerViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View parent, int position);
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(View parent, int position);
    }
}
