package com.myyg.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.adapter.CommonAdapter;
import com.myyg.adapter.ViewHolder;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UserShareModel;
import com.myyg.photopicker.PhotoPagerActivity;
import com.myyg.photopicker.PhotoPickerActivity;
import com.myyg.photopicker.utils.PhotoPickerIntent;
import com.myyg.utils.BitmapHelper;
import com.myyg.utils.ClickFilter;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.FileHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddShareActivity extends BaseActivity implements Handler.Callback {
    public static final String SHARE_GOODS_TAG = "share_goods_tag";
    public static final int CAMERA_REQUEST_CODE = 0x00;
    public static final int PREVIEW_REQUEST_CODE = 0x01;
    private int imageSize;

    private GridView gv_picture;

    private ArrayList<String> listPicture = new ArrayList<>();

    protected CommonAdapter<String> adapter;

    private int picNum = 6;

    private Handler mHandler;

    private EditText et_title;

    private EditText et_content;

    private TextView tv_goods_name;

    private UserShareModel.ShareOrderModel model;

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_share);
        this.gv_picture = (GridView) findViewById(R.id.gv_picture);
        this.et_title = (EditText) this.findViewById(R.id.et_title);
        this.et_content = (EditText) this.findViewById(R.id.et_content);
        this.tv_goods_name = (TextView) this.findViewById(R.id.tv_goods_name);
    }

    @Override
    public void initData() {
        this.model = (UserShareModel.ShareOrderModel) this.getIntent().getSerializableExtra(SHARE_GOODS_TAG);
        this.listPicture.add("");
        this.imageSize = BaseApplication.WINDOW_WIDTH / 3;
        this.mHandler = new Handler(this);
    }

    @Override
    public void fillView() {
        this.setToolBar("分享晒单");
        this.tv_goods_name.setText(this.model.getTitle());
        this.adapter = new CommonAdapter<String>(this.mContext, listPicture, R.layout.item_add_share) {
            @Override
            public void convert(ViewHolder helper, String item, int position) {
                try {
                    ImageView iv_photo = helper.getView(R.id.iv_photo);
                    if (TextUtils.isEmpty(item)) {
                        Glide.with(mContext).load(R.mipmap.ic_picture_add).thumbnail(0.1f)
                                .dontAnimate().dontTransform().override(imageSize, imageSize)
                                .placeholder(R.mipmap.ic_picture_add)
                                .error(R.mipmap.ic_picture_add).into(iv_photo);
                        iv_photo.setOnClickListener((view) -> {
                            if (position != listPicture.size() - 1) {
                                return;
                            }
                            if (listPicture.size() == picNum + 1) {
                                UIHelper.toastMessage(mContext, MessageFormat.format("您最多可以添加{0}张图片", picNum));
                                return;
                            }
                            addPicture();
                        });
                        return;
                    }
                    iv_photo.setOnClickListener((view) -> {
                        ArrayList<String> listPhoto = (ArrayList<String>) listPicture.clone();
                        listPhoto.remove("");
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList(PhotoPagerActivity.EXTRA_PHOTOS, listPhoto);
                        bundle.putInt(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
                        bundle.putBoolean(PhotoPagerActivity.EXTRA_SHOW_DELETE, true);
                        UIHelper.startActivityForResult(mContext, PhotoPagerActivity.class, PREVIEW_REQUEST_CODE, bundle);
                    });
                    Uri uri = Uri.fromFile(new File(item));
                    Glide.with(mContext)
                            .load(uri)
                            .thumbnail(0.5f)
                            .dontAnimate()
                            .dontTransform()
                            .override(imageSize, imageSize)
                            .placeholder(R.mipmap.ic_list_default_lightgray_small)
                            .error(R.mipmap.ic_list_default_lightgray_small)
                            .into(iv_photo);
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                }
            }
        };
        this.gv_picture.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                this.save();
                break;
            case R.id.tv_see:
                Bundle bundle = new Bundle();
                bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, this.model.getShopid());
                UIHelper.startActivity(this.mContext, GoodsDetailsActivity.class, bundle);
                break;
        }
    }

    /**
     * 添加相片
     */
    private void addPicture() {
        int totalPhoto = this.picNum - this.listPicture.size() + 1;
        PhotoPickerIntent intent = new PhotoPickerIntent(this.mContext);
        intent.setPhotoCount(totalPhoto);
        intent.setShowCamera(true);
        ArrayList<String> listDir = new ArrayList<>();
        intent.setFilterDir(listDir);
        intent.setShowTips(true);
        intent.setShowDone(true);
        UIHelper.startActivityForResult(this.mContext, intent, CAMERA_REQUEST_CODE, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            List<String> listAddPhoto = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            listPicture.addAll(listPicture.size() - 1, listAddPhoto);
            if (listPicture.size() > picNum) {
                listPicture.remove("");
            }
            adapter.notifyDataSetChanged();
            return;
        }
        if (requestCode == PREVIEW_REQUEST_CODE) {
            List<String> listPhoto = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            if (listPhoto.size() == picNum) {
                return;
            }
            listPicture.clear();
            listPicture.addAll(listPhoto);
            if (listPicture.size() < picNum) {
                listPicture.add("");
            }
            adapter.notifyDataSetChanged();
            return;
        }
    }

    /**
     *
     */
    private void save() {
        try {
            if (ClickFilter.filter()) {
                return;
            }
            String title = this.et_title.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                UIHelper.toastMessage(mContext, "请输入晒单标题");
                return;
            }
            String content = this.et_content.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                UIHelper.toastMessage(mContext, "请输入晒单内容");
                return;
            }
            if (this.listPicture.size() == 1) {
                UIHelper.toastMessage(mContext, "请添加图片");
                return;
            }
            UIHelper.showLoading(mContext, "正在提交，请稍等...");
            this.mHandler.post(() -> {
                List<String> listThumbnail = getThumbnail();
                Message message = Message.obtain();
                message.obj = listThumbnail;
                message.what = 0x01;
                this.mHandler.sendMessage(message);
            });
        } catch (Exception ex) {
            UIHelper.hideLoading();
        }
    }

    /**
     * @return
     */
    private List<String> getThumbnail() {
        String dirPath = getResources().getString(R.string.share_dir) + "/thumbnail";
        List<String> listThumbnail = new ArrayList<>();
        ArrayList<String> listPhoto = (ArrayList<String>) listPicture.clone();
        listPhoto.remove("");
        for (int i = 0; i < listPhoto.size(); i++) {
            String filePath = listPhoto.get(i);
            int degree = CommonHelper.getPictureDegree(filePath);
            double size = FileHelper.getfileSize(filePath, FileHelper.SIZETYPE_MB);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            // 如果原图大于4M，则进行图片压缩
            if (size > 1) {
                bitmap = BitmapHelper.compressImage(bitmap, 1 * 1024);
            }
            if (degree != 0) {
                bitmap = CommonHelper.rotateBitmapByDegree(bitmap, degree);
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            int number = CommonHelper.getRandomNumber(1000, 9999);
            String fileUrl = FileHelper.save(bitmap, dirPath, "IMG_" + timeStamp + "_" + number + ".jpg");
            listThumbnail.add(fileUrl);
        }
        return listThumbnail;
    }

    /**
     * 删除上传临时创建的缩略图
     */
    private void deleteThumbnail() {
        String dirPath = getResources().getString(R.string.share_dir);
        dirPath = MessageFormat.format("{0}/myyg/{1}", FileHelper.getBasePath(), dirPath);
        File dir = new File(dirPath);
        FileHelper.delete(dir);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0x01:
                List<String> listThumbnail = (List<String>) msg.obj;
                this.addShare(listThumbnail);
                break;
        }
        return true;
    }

    /**
     * @param listThumbnail
     */
    private void addShare(List<String> listThumbnail) {
        String title = this.et_title.getText().toString().trim();
        String content = this.et_content.getText().toString().trim();
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.getHeaders().remove(0);
        params.addBodyParameter("shopid", this.model.getShopid() + "");
        params.addBodyParameter("title", title);
        params.addBodyParameter("content", content);
        for (int i = 0; i < listThumbnail.size(); i++) {
            String filePath = listThumbnail.get(i);
            params.addBodyParameter(MessageFormat.format("image[{0}]", i), new File(filePath), " image/jpeg");
        }
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_ADD_SHARE, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        UIHelper.toastMessage(mContext, message.getMsg());
                        return;
                    }
                    UIHelper.toastMessage(mContext, "晒单成功");
                    finish();
                } catch (Exception ex) {
                    MyLog.e(TAG, ex.getMessage());
                } finally {
                    deleteThumbnail();
                    UIHelper.hideLoading();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                MyLog.e(TAG, s);
                UIHelper.hideLoading();
            }
        });
    }
}