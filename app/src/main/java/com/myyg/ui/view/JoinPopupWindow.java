package com.myyg.ui.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.myyg.R;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.NumberView;
import com.rey.material.widget.Button;

/**
 * Created by JOHN on 2016/7/18.
 */
public class JoinPopupWindow extends PopupWindow {
    private Context mContext;
    private OnJoinPopupWindowListener listener;
    private NumberView nv_number;
    private int maxNumber;

    public JoinPopupWindow(Context context, int maxNumber, OnJoinPopupWindowListener listener) {
        super(context);
        this.mContext = context;
        this.maxNumber = maxNumber;
        this.listener = listener;
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_join_popupwindow, null);
        this.setContentView(layout);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.nv_number = (NumberView) layout.findViewById(R.id.nv_number);
        this.nv_number.setWidth(CommonHelper.dp2px(this.mContext, 50));
        this.nv_number.setEnabled(true);
        this.nv_number.setMaxNumber(this.maxNumber);
        //this.getContentView().setOnClickListener(v -> dismiss());
        ImageView iv_close = (ImageView) layout.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(v -> dismiss());
        Button btn_bidding = (Button) layout.findViewById(R.id.btn_bidding);
        btn_bidding.setOnClickListener(v -> {
            if (this.listener != null) {
                int number = this.nv_number.getNumber();
                if (number > this.maxNumber) {
                    UIHelper.toastMessage(this.mContext, "请正常输入参与人次");
                    return;
                }
                listener.onBidding(nv_number.getNumber());
                this.dismiss();
            }
        });
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public interface OnJoinPopupWindowListener {
        void onBidding(int joinNumber);
    }
}
