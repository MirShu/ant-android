package com.myyg.ui.view;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.utils.CommonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOHN on 2016/6/1.
 */
public class PartakeRecordView extends LinearLayout {
    private Context mContext;
    private List<String> listData;

    private RecyclerView rv_partake_record;

    private RecyclerAdapter<String> adapter;

    public PartakeRecordView(Context context) {
        this(context, null);
    }

    public PartakeRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PartakeRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_partake_record, null, false);
        this.rv_partake_record = (RecyclerView) layout.findViewById(R.id.rv_partake_record);
        this.loadData();
        this.adapter = new RecyclerAdapter<String>(this.mContext, listData, R.layout.item_partake_record) {
            @Override
            public void convert(RecyclerViewHolder helper, String item, int position) {
                View view_split = helper.getView(R.id.view_split);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view_split.getLayoutParams();
                layoutParams.height = CommonHelper.dp2px(mContext, 10);
                view_split.setLayoutParams(layoutParams);
            }
        };
        this.rv_partake_record.setHasFixedSize(true);
        this.rv_partake_record.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_partake_record.setAdapter(adapter);
    }

    /**
     *
     */
    private void loadData() {
        this.listData = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            this.listData.add(String.valueOf(i));
        }
    }
}
