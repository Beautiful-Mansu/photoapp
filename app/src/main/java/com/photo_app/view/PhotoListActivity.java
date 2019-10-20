package com.photo_app.view;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.photo_app.R;
import com.photo_app.model.data.Size;
import com.photo_app.presenter.PhotoListPresenter;

import java.util.List;

public class PhotoListActivity extends AppCompatActivity implements PhotoListPresenter.ViewContractListener {

    private ImageAdapter mImageAdapter;
    private FrameLayout mContainer;
    private ProgressBar mProgressBar;
    private ImageView mErrorImageView;
    private PhotoListPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        mProgressBar = findViewById(R.id.xProgressBar);
        mContainer = findViewById(R.id.xContainer);
        mErrorImageView = findViewById(R.id.xErrorImage);
        GridView gridView = findViewById(R.id.gridView);
        mImageAdapter = new ImageAdapter();
        gridView.setAdapter(mImageAdapter);
        presenter = new PhotoListPresenter(this);
        presenter.start();
    }


    @Override
    public void updateProgress(boolean shouldDisplay) {
        int visibility = shouldDisplay ? View.VISIBLE : View.GONE;
        mContainer.setVisibility(visibility);
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void updateUI(List<Size> dataSet) {
        updateProgress(false);
        if (!dataSet.isEmpty()) {
            mImageAdapter.updateData(dataSet);
        } else {
            // display toast if the main or photos api throws error.
            setErrorImage();
        }
    }

    /**
     * display error image if main or photos api throws error.
     */
    private void setErrorImage() {
        mContainer.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.error_message , Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }
}