package com.myyg.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.constant.SysEnums;
import com.myyg.model.GoodsModel;
import com.myyg.ui.activity.HisPersonalCenterActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.rey.material.widget.Button;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by JOHN on 2016/8/1.
 */
public class CloudRecordsAdapter extends BaseRecyclerAdapter<RecyclerViewHolder> {
    private final static String TAG = CloudRecordsAdapter.class.getSimpleName();

    // 进行中
    private final static int PROCESS_00 = 0x00;

    // 已揭晓
    private final static int PROCESS_01 = 0x01;

    // 揭晓中
    private final static int PROCESS_02 = 0x02;

    private Context mContext;

    private RecyclerView mRecyclerView;

    private List<GoodsModel> listData;

    protected LayoutInflater mInflater;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private OnItemClickListener itemClickListener;

    public CloudRecordsAdapter(Context context, RecyclerView recyclerView, List<GoodsModel> listData) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mRecyclerView = recyclerView;
        this.listData = listData;
    }

    @Override
    public RecyclerViewHolder getViewHolder(View view) {
        return new RecyclerViewHolder(view);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = null;
        switch (viewType) {
            case PROCESS_00:
                view = this.mInflater.inflate(R.layout.item_his_not_winning, parent, false);
                break;
            case PROCESS_01:
                view = this.mInflater.inflate(R.layout.item_his_winning, parent, false);
                break;
            case PROCESS_02:
                view = this.mInflater.inflate(R.layout.item_his_winning, parent, false);
                break;
        }
        if (view != null) {
            RecyclerViewHolder holder = new RecyclerViewHolder(view);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position, boolean isItem) {
        GoodsModel model = this.listData.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (this.itemClickListener != null) {
                this.itemClickListener.onClick(v, position);
            }
        });
        Button btn_buy = holder.getView(R.id.btn_buy);
        btn_buy.setOnClickListener(v -> {
            if (this.itemClickListener != null) {
                this.itemClickListener.onClick(v, position);
            }
        });
        ImageView iv_price = holder.getView(R.id.iv_price);
        iv_price.setVisibility(View.GONE);
        if (model.getYunjiage() > 1) {
            iv_price.setVisibility(View.VISIBLE);
        }
        String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, model.getThumb());
        if (model.getProcess() == SysEnums.EnumGoodsStatus.Ongoing.getValue()) {
            holder.setImageUrl(R.id.iv_thumb, imgUrl);
            holder.setText(R.id.tv_title, MessageFormat.format("(第{0}期){1}", String.valueOf(model.getQishu()), model.getTitle()));
            holder.setText(R.id.tv_zongrenshu, MessageFormat.format("总需人次：{0}", String.valueOf(model.getZongrenshu())));
            RoundCornerProgressBar rc_schedule = holder.getView(R.id.rc_schedule);
            rc_schedule.setMax(100);
            rc_schedule.setProgress(model.getCanyurenshu() * 100 / model.getZongrenshu());
            holder.setText(R.id.tv_shenyurenshu, Html.fromHtml(MessageFormat.format("剩余：<font color=\"#0171bb\">{0}</font>", String.valueOf(model.getShenyurenshu()))));
            return;
        }
        holder.setImageUrl(R.id.iv_thumb, imgUrl);
        holder.setText(R.id.tv_title, MessageFormat.format("(第{0}期){1}", String.valueOf(model.getQishu()), model.getTitle()));
        holder.setText(R.id.tv_qishu, MessageFormat.format("商品期号：{0}", String.valueOf(model.getQishu())));
        holder.setText(R.id.tv_canyurenshu, Html.fromHtml(MessageFormat.format("本次参与：<font color=\"#f95667\">{0}</font>人次", String.valueOf(model.getCanyurenshu()))));
        holder.setText(R.id.tv_q_user, Html.fromHtml(MessageFormat.format("获得者：<font color=\"#0171bb\">{0}</font>", model.getQ_user())));
        ImageView iv_goodluck = holder.getView(R.id.iv_goodluck);
        iv_goodluck.setVisibility(View.GONE);
        if (model.getWin() == 1) {
            iv_goodluck.setVisibility(View.VISIBLE);
        } else {
            TextView tv_q_user = holder.getView(R.id.tv_q_user);
            tv_q_user.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(HisPersonalCenterActivity.HIS_USER_ID, model.getQ_uid());
                UIHelper.startActivity((Activity) mContext, HisPersonalCenterActivity.class, bundle);
            });
        }
    }

    @Override
    public int getAdapterItemViewType(int position) {
        GoodsModel model = this.listData.get(position);
        if (model.getProcess() == SysEnums.EnumGoodsStatus.Ongoing.getValue()) {
            return PROCESS_00;
        }
        if (model.getProcess() == SysEnums.EnumGoodsStatus.Announced.getValue()) {
            return PROCESS_01;
        }
        return PROCESS_02;
    }

    @Override
    public int getAdapterItemCount() {
        return this.listData.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View parent, int position);
    }
}