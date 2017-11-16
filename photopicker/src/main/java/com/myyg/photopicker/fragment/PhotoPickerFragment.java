package com.myyg.photopicker.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.myyg.photopicker.PhotoPagerActivity;
import com.myyg.photopicker.PhotoPickerActivity;
import com.myyg.photopicker.R;
import com.myyg.photopicker.adapter.PhotoGridAdapter;
import com.myyg.photopicker.adapter.PopupDirectoryListAdapter;
import com.myyg.photopicker.entity.Photo;
import com.myyg.photopicker.entity.PhotoDirectory;
import com.myyg.photopicker.event.OnPhotoClickListener;
import com.myyg.photopicker.utils.ImageCaptureManager;
import com.myyg.photopicker.utils.MediaStoreHelper;

import static android.app.Activity.RESULT_OK;
import static com.myyg.photopicker.PhotoPickerActivity.EXTRA_SHOW_GIF;
import static com.myyg.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerFragment extends Fragment {
    public final static int PREVIEW_DONE_REQUEST_CODE = 0x88;
    private ImageCaptureManager captureManager;
    private PhotoGridAdapter photoGridAdapter;

    private PopupDirectoryListAdapter listAdapter;
    private List<PhotoDirectory> directories;

    private int SCROLL_THRESHOLD = 30;
    private ArrayList<String> listDir;
    private OnPhotoPickerListener onPhotoPickerListener;
    private RelativeLayout rl_button;
    private int business;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        directories = new ArrayList<>();

        captureManager = new ImageCaptureManager(getActivity());

        Bundle mediaStoreArgs = new Bundle();
        if (getActivity() instanceof PhotoPickerActivity) {
            mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, ((PhotoPickerActivity) getActivity()).isShowGif());
            listDir = ((PhotoPickerActivity) getActivity()).getListDir();
        }
        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs, listDir,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        directories.clear();
                        directories.addAll(dirs);
                        photoGridAdapter.notifyDataSetChanged();
                        listAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        final View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);

        photoGridAdapter = new PhotoGridAdapter(getActivity(), directories);
        listAdapter = new PopupDirectoryListAdapter(getActivity(), directories);
        rl_button = (RelativeLayout) rootView.findViewById(R.id.rl_button);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final TextView btnDirectory = (TextView) rootView.findViewById(R.id.tv_dir);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(dm.widthPixels);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setAnchorView(btnDirectory);
        listPopupWindow.setAdapter(listAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        listPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                PhotoDirectory directory = directories.get(position);
                btnDirectory.setText(directory.getName());
                for (PhotoDirectory item : directories) {
                    item.setIsSelected(false);
                }
                directories.get(position).setIsSelected(true);
                listAdapter.notifyDataSetChanged();
                photoGridAdapter.setCurrentDirectoryIndex(position);
                photoGridAdapter.notifyDataSetChanged();
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });

        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 : position;
                List<String> photos = photoGridAdapter.getCurrentPhotoPaths();
                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                ImagePagerFragment imagePagerFragment = ImagePagerFragment.newInstance(photos, index, screenLocation, v.getWidth(), v.getHeight());
                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
            }
        });

        photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
                                                      @Override
                                                      public void onClick(View view) {
                                                          try {
                                                              // 验证权限
                                                              if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                                  ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0x01);
                                                              } else {
                                                                  Intent intent = captureManager.dispatchTakePictureIntent();
                                                                  startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                                                              }
                                                          } catch (IOException e) {
                                                              Toast.makeText(getActivity(), "请打开相机访问权限", Toast.LENGTH_SHORT).show();
                                                          }
                                                      }
                                                  }

        );

        btnDirectory.setOnClickListener(new

                                                OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (listPopupWindow.isShowing()) {
                                                            listPopupWindow.dismiss();
                                                        } else if (!getActivity().isFinishing()) {
                                                            listPopupWindow.setHeight(Math.round(rootView.getHeight() * 0.7f));
                                                            listPopupWindow.show();
                                                            WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                                                            params.alpha = 0.7f;
                                                            getActivity().getWindow().setAttributes(params);
                                                        }
                                                    }
                                                }

        );

        TextView btnPreview = (TextView) rootView.findViewById(R.id.tv_preview);
        btnPreview.setOnClickListener(new

                                              OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      ArrayList<String> photos = photoGridAdapter.getSelectedPhotoPaths();
                                                      if (photos.size() == 0) {
                                                          return;
                                                      }
                                                      Bundle bundle = new Bundle();
                                                      bundle.putStringArrayList(PhotoPagerActivity.EXTRA_PHOTOS, photos);
                                                      Intent intent = new Intent();
                                                      intent.setClass(getActivity(), PhotoPagerActivity.class);
                                                      intent.putExtras(bundle);
                                                      startActivityForResult(intent, PREVIEW_DONE_REQUEST_CODE);
                                                  }
                                              }

        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()

                                         {
                                             @Override
                                             public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                 super.onScrolled(recyclerView, dx, dy);
                                                 // Log.d(">>> Picker >>>", "dy = " + dy);
                                                 if (Math.abs(dy) > SCROLL_THRESHOLD) {
                                                     Glide.with(getActivity()).pauseRequests();
                                                 } else {
                                                     Glide.with(getActivity()).resumeRequests();
                                                 }
                                             }

                                             @Override
                                             public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                 if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                                     Glide.with(getActivity()).resumeRequests();
                                                 }
                                             }
                                         }

        );
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO) {
            captureManager.galleryAddPic();
            String path = captureManager.getCurrentPhotoPath();
            if (directories.size() > 0) {
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                photoGridAdapter.notifyDataSetChanged();
            }
            if (onPhotoPickerListener != null) {
                onPhotoPickerListener.captureSuccess(path);
            }
            return;
        }
        if (requestCode == PREVIEW_DONE_REQUEST_CODE) {
            String resultKey = data.getStringExtra(PhotoPagerActivity.RETURN_RESULT_KEY);
            if (onPhotoPickerListener != null && resultKey != null && resultKey.equals("ok")) {
                onPhotoPickerListener.previewDone();
            }
            return;
        }
    }

    public void setBusinessCode(int businessCode) {
        this.business = businessCode;
    }


    public PhotoGridAdapter getPhotoGridAdapter() {
        return photoGridAdapter;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        return photoGridAdapter.getSelectedPhotoPaths();
    }

    /**
     * 设置回调事件
     *
     * @param onPhotoPickerListener
     */
    public void setOnPhotoPickerListener(OnPhotoPickerListener onPhotoPickerListener) {
        this.onPhotoPickerListener = onPhotoPickerListener;
    }

    public void setColor(String color) {
        if (this.rl_button != null) {
            this.rl_button.setBackgroundColor(Color.parseColor(color));
        }
    }

    /**
     * 定义拍照事件监听
     */
    public interface OnPhotoPickerListener {
        /**
         * 当前文件保存路径
         *
         * @param path
         */
        void captureSuccess(String path);

        /**
         * 预览照片（操作完成）
         */
        void previewDone();
    }
}
