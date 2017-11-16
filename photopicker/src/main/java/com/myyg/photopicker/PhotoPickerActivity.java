package com.myyg.photopicker;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.myyg.photopicker.entity.Photo;
import com.myyg.photopicker.event.OnItemCheckListener;
import com.myyg.photopicker.event.OnPhotoClickListener;
import com.myyg.photopicker.fragment.ImagePagerFragment;
import com.myyg.photopicker.fragment.PhotoPickerFragment;
import com.myyg.photopicker.utils.ClickFilter;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {

    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_DONE = "SHOW_DONE";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String FILTER_DIR = "FILTER_DIR";
    public final static String EXTRA_SHOT_TIPS = "SHOW_TIPS";
    public final static String THEME_COLOR = "theme_color";
    private Toolbar mToolbar;
    private TextView tv_preview;

    private MenuItem menuDoneItem;

    public final static int DEFAULT_MAX_COUNT = 9;

    private int maxCount = DEFAULT_MAX_COUNT;

    /**
     * to prevent multiple calls to inflate menu
     */
    private boolean menuIsInflated = false;

    private boolean showGif = false;

    private boolean showTips = false;

    private ArrayList<String> listDir;

    private String color = "#35C1B6";

    private int business = 0x01;

    private boolean isShowDone = false;

    private TextView tv_title;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        String color = getIntent().getStringExtra(THEME_COLOR);
        this.isShowDone = this.getIntent().getBooleanExtra(EXTRA_SHOW_DONE, false);
        if (!TextUtils.isEmpty(color)) {
            this.color = color;
            business = 0x02;
        }
        ArrayList<String> listDir = (ArrayList<String>) getIntent().getSerializableExtra(FILTER_DIR);
        setListDir(listDir);
        setShowGif(showGif);
        boolean showTips = getIntent().getBooleanExtra(EXTRA_SHOT_TIPS, false);
        setShowTips(showTips);
        setContentView(R.layout.activity_photo_picker);
        this.mToolbar = (Toolbar) this.findViewById(R.id.bar_title);
        this.tv_preview = (TextView) this.findViewById(R.id.tv_preview);
        this.tv_preview.setAlpha(0.7f);
//        tv_head_right = (TextView) findViewById(R.id.tv_head_right);
//        RelativeLayout parentView = (RelativeLayout) (this.tv_head_right.getParent());
//        if (parentView != null) {
//            parentView.setBackgroundColor(Color.parseColor(this.color));
//        }
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }
        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        String title=maxCount > 1 ? getString(R.string.picker_done_with_count, 0, maxCount) : getString(R.string.picker_images);
        this.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(false);
        tv_title = (TextView) this.mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(title);
        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);
        pickerFragment.getPhotoGridAdapter().setShowChecked((maxCount > 1 || isShowTips()) ? true : false);
        pickerFragment.getPhotoGridAdapter().setShowCamera(showCamera);
        pickerFragment.getPhotoGridAdapter().setBusinessCode(business);
        if (!TextUtils.isEmpty(color)) {
            pickerFragment.setColor(color);
        }
        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {
                if (ClickFilter.filter()) {
                    return false;
                }
                int total = selectedItemCount + (isCheck ? -1 : 1);
                menuDoneItem.setEnabled(false);
                menuDoneItem.getIcon().setAlpha(100);
                tv_preview.setAlpha(0.7f);
                if (total > 0) {
                    menuDoneItem.setEnabled(true);
                    menuDoneItem.getIcon().setAlpha(255);
                    tv_preview.setAlpha(1);
                }
                if (maxCount <= 1 && !isShowTips()) {
                    List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
                    if (!photos.contains(photo)) {
                        photos.clear();
                        pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
                    }
                    return true;
                }
                if (total > maxCount) {
                    Toast.makeText(getActivity(), getString(R.string.picker_over_max_count_tips, maxCount), LENGTH_LONG).show();
                    return false;
                }
                setTip(total);
                menuDoneItem.setTitle(getString(R.string.picker_done_with_count, total, maxCount));
                return true;
            }
        });
        pickerFragment.getPhotoGridAdapter().setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                if (maxCount <= 1 && !isShowTips()) {
                    done();
                }
            }
        });
        pickerFragment.setOnPhotoPickerListener(new PhotoPickerFragment.OnPhotoPickerListener() {
            @Override
            public void captureSuccess(String path) {
                /**
                 * 如果为单张图片则直接返回给调用方
                 */
//                if (maxCount == 1 && !isShowTips()) {
//                    Intent intent = new Intent();
//                    ArrayList<String> selectedPhotos = new ArrayList<>();
//                    selectedPhotos.add(path);
//                    intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
                Intent intent = new Intent();
                ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.add(path);
                intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void previewDone() {
                done();
            }
        });
    }

    /**
     * @param total
     */
    private void setTip(int total) {
        String title = maxCount > 1 ? getString(R.string.picker_done_with_count, total, maxCount) : getString(R.string.picker_images);
        this.tv_title.setText(title);
    }


    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            imagePagerFragment.runExitAnimation(new Runnable() {
                public void run() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menuIsInflated) {
            getMenuInflater().inflate(R.menu.menu_picker, menu);
            menuDoneItem = menu.findItem(R.id.action_done);
            menuDoneItem.setEnabled(false);
            menuIsInflated = true;
            this.menuDoneItem.getIcon().setAlpha(70);
            if (maxCount == 1 && !this.isShowDone) {
                this.menuDoneItem.setVisible(false);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_done) {
            done();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 完成图片选择
     */
    public void done() {
        Intent intent = new Intent();
        ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
        intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
        setResult(RESULT_OK, intent);
        finish();
    }

    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

    public ArrayList<String> getListDir() {
        return listDir;
    }

    public void setListDir(ArrayList<String> listDir) {
        this.listDir = listDir;
    }

    public boolean isShowTips() {
        return showTips;
    }

    public void setShowTips(boolean showTips) {
        this.showTips = showTips;
    }
}
