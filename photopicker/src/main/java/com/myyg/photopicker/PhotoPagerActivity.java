package com.myyg.photopicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import com.myyg.photopicker.fragment.ImagePagerFragment;

/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private ImagePagerFragment pagerFragment;

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";
    public final static String EXTRA_SHOW_DELETE = "show_delete";
    public final static String RETURN_RESULT_KEY = "return_result_key";

    private ActionBar actionBar;
    private boolean showDelete;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_pager);

        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, false);

        pagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        pagerFragment.setPhotos(paths, currentItem);

        mToolbar = (Toolbar) findViewById(R.id.bar_title);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        updateActionBarTitle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setElevation(25);
        }

        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_preview, menu);
        if (!showDelete) {
            MenuItem menuDelete = menu.findItem(R.id.action_delete);
            menuDelete.setVisible(false);
        } else {
            MenuItem menuDone = menu.findItem(R.id.action_done);
            menuDone.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_done) {
            Intent intent = new Intent();
            intent.putExtra(RETURN_RESULT_KEY, "ok");
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_delete) {
            int index = pagerFragment.getCurrentItem();
            pagerFragment.getPaths().remove(index);
            pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            if (pagerFragment.getPaths().size() < 1) {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    public void updateActionBarTitle() {
        String title = getString(R.string.picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1, pagerFragment.getPaths().size());
        actionBar.setTitle(title);
        actionBar.setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) this.mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(title);
    }
}
