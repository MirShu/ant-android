package com.myyg.photopicker.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.myyg.photopicker.R;
import com.myyg.photopicker.entity.Photo;
import com.myyg.photopicker.entity.PhotoDirectory;
import com.myyg.photopicker.event.OnItemCheckListener;
import com.myyg.photopicker.event.OnPhotoClickListener;
import com.myyg.photopicker.utils.MediaStoreHelper;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private LayoutInflater inflater;

    private Context mContext;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;

    private boolean hasCamera = true;

    private final int imageSize;
    private boolean isShowChecked = true;
    private int business;

    public PhotoGridAdapter(Context context, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        business = 0x01;
        imageSize = widthPixels / 3;
    }

    public void setBusinessCode(int businessCode) {
        this.business = businessCode;
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            List<Photo> photos = getCurrentPhotos();
            final Photo photo;

            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }

            Glide.with(mContext)
                    .load(new File(photo.getPath()))
                    .centerCrop()
                    .dontAnimate()
                    .thumbnail(0.5f)
                    .override(imageSize, imageSize)
                    .placeholder(R.drawable.ic_picture_default_lightgray)
                    .error(R.drawable.ic_picture_default_lightgray)
                    .into(holder.ivPhoto);

            final boolean isChecked = isSelected(photo);
            View view = holder.vSelected;
            view.setVisibility(View.VISIBLE);
            if (!isShowChecked) {
                view.setVisibility(View.GONE);
            }
            holder.ivPhoto.setSelected(isChecked);
            holder.vSelected.setBackground(null);
            if (business == 0x02) {
                holder.vSelected.setImageResource(R.drawable.photo_checkbox_red_bg);
            }
            holder.vSelected.setSelected(isChecked);
            if (isChecked) {
                holder.photo_mask.setVisibility(View.VISIBLE);
            } else {
                holder.photo_mask.setVisibility(View.GONE);
            }
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 新增图片点击事件
                    boolean isEnable = true;
                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked, getSelectedPhotos().size());
                    }
                    if (onPhotoClickListener != null) {
                        if (isEnable) {
                            toggleSelection(photo);
                            notifyItemChanged(position);
                        }
                        //toggleSelection(photo);
                        onPhotoClickListener.onClick(view, position, showCamera());
                    }

                }
            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isEnable = true;
                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.OnItemCheck(position, photo, isChecked, getSelectedPhotos().size());
                    }
                    if (isEnable) {
                        toggleSelection(photo);
                        notifyItemChanged(position);
                    }
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_local_see_white_24dp);
            holder.ivPhoto.setBackgroundColor(Color.parseColor("#333333"));
            int margins = dip2px(mContext, 1);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.ivPhoto.getLayoutParams();
            layoutParams.setMargins(margins, margins, margins, margins);
            holder.ivPhoto.setLayoutParams(layoutParams);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        int photosCount = photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView vSelected;
        private View photo_mask;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = (ImageView) itemView.findViewById(R.id.v_selected);
            photo_mask = itemView.findViewById(R.id.photo_mask);
        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

        for (Photo photo : selectedPhotos) {
            selectedPhotoPaths.add(photo.getPath());
        }

        return selectedPhotoPaths;
    }


    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }


    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

    public void setShowChecked(boolean isShowChecked) {
        this.isShowChecked = isShowChecked;
    }
}
