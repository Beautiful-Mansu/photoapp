package com.photo_app.view;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.photo_app.R;
import com.photo_app.model.data.Size;
import com.photo_app.presenter.PhotoListPresenter;

import java.util.List;

public class PhotoListActivity extends AppCompatActivity implements PhotoListPresenter.ViewContractListener {

    private ImageAdapter mImageAdapter;
    private FrameLayout mProgressBar;
    private PhotoListPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        GridView mGridView = findViewById(R.id.gridView);
        mProgressBar = findViewById(R.id.progress_circular);
        mImageAdapter = new ImageAdapter();
        mGridView.setAdapter(mImageAdapter);
        presenter = new PhotoListPresenter(this);
        presenter.start();
    }


    @Override
    public void updateProgress(boolean shouldDisplay) {
        int visibility = shouldDisplay ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void updateUI(List<Size> dataSet) {
        mImageAdapter.updateData(dataSet);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }
}