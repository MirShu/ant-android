package com.myyg.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysKeys;
import com.myyg.constant.URLS;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.photopicker.PhotoPickerActivity;
import com.myyg.photopicker.utils.PhotoPickerIntent;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.FileHelper;
import com.myyg.utils.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/13.
 */
public class PersonalDataActivity extends BaseActivity {

    private final static int REQUEST_SELECT_CODE = 0x01;

    private final static int REQUEST_CROP_CODE = 0x02;

    private ImageView riv_avatar;

    private UserModel user = BaseApplication.getUser();

    private String filePath;

    private String baseUrl = CommonHelper.getStaticBasePath();

    public void initView() {
        setContentView(R.layout.activity_personal_data);
        this.riv_avatar = (ImageView) findViewById(R.id.riv_avatar);
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        setToolBar("个人中心");
        UserModel user = BaseApplication.getUser();
        String avatar = MessageFormat.format("{0}/{1}", this.baseUrl, user.getAvatar());
        ImageLoader.getInstance().displayImage(avatar, this.riv_avatar, ImageLoaderEx.getDisplayImageOptions());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_address_management:
                UIHelper.startActivity(PersonalDataActivity.this, AddAddressActivity.class);
                break;
            case R.id.rl_nick_name:
                UIHelper.startActivity(PersonalDataActivity.this, ModifiedNicknameActivity.class);
                break;
            case R.id.riv_avatar:
                this.choicePicture();
                break;
            case R.id.rl_common_problem:
                this.choicePicture();
                break;
        }
    }

    /**
     * 选择图片
     */
    private void choicePicture() {
        PhotoPickerIntent intent = new PhotoPickerIntent(this.mContext);
        intent.setPhotoCount(1);
        intent.setShowCamera(true);
        intent.setShowGif(false);
        ArrayList<String> listDir = new ArrayList<>();
        intent.setFilterDir(listDir);
        startActivityForResult(intent, REQUEST_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> listPhoto = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    if (listPhoto.size() > 0) {
                        String photo = listPhoto.get(0);
                        Uri uri = Uri.fromFile(new File(photo));
                        startPhotoZoom(uri);
                    }
                }
                break;
            case REQUEST_CROP_CODE:
                this.uploadPhoto(filePath);
                break;
        }
    }

    /**
     * 图片裁切
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        String fileName = this.user.getUsername();
        // 兼容现在安装用户处理（V2.0.0）
        if (TextUtils.isEmpty(fileName)) {
            String photo = user.getAvatar();
            fileName = MessageFormat.format("{0}.jpg", String.valueOf(System.currentTimeMillis()));
            if (!TextUtils.isEmpty(photo)) {
                fileName = photo.substring(photo.lastIndexOf('/') + 1);
            }
            BaseApplication.sharedPut(SysKeys.SHARED_USER_AVATAR, fileName);
        }
        String dirPath = MessageFormat.format("{0}/{1}/", FileHelper.getBasePath(), "myyg/userphoto");
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        filePath = MessageFormat.format("{0}{1}", dirPath, fileName);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_CROP_CODE);
    }

    /**
     * 上传用户图像
     */
    private void uploadPhoto(final String filePath) {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        params.getHeaders().remove(0);
        params.addBodyParameter("file", new File(filePath));
        http.send(HttpRequest.HttpMethod.POST, URLS.USER_UPLOAD_AVATAR, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                UIHelper.showLoading(mContext, "正在保存图片，请稍等...");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                UIHelper.hideLoading();
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != 0) {
                    UIHelper.toastMessage(mContext, "图像上传失败");
                    return;
                }
                String photo = result.getData();
                riv_avatar.setImageURI(Uri.parse(filePath));
                user.setAvatar(photo);
                BaseApplication.sharedPut(SysKeys.SHARED_USER_AVATAR, photo);
                BaseApplication.sharedLogin(user);
                UIHelper.toastMessage(mContext, "图像上传成功");
                FileHelper.delete(new File(filePath));
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIHelper.hideLoading();
                UIHelper.toastMessage(mContext, R.string.network_anomaly);
            }
        });
    }
}
