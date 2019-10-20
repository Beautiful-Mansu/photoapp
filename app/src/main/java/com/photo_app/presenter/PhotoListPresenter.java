package com.photo_app.presenter;

import android.content.Context;

import com.photo_app.model.data.PhotoResponse;
import com.photo_app.model.data.Size;
import com.photo_app.model.network.PhotoRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PhotoListPresenter {

    private Context mContext;
    private ViewContractListener mViewListener;
    private final CompositeDisposable mCompositeDisposable;


    public PhotoListPresenter(Context context) {
        mContext = context;
        mCompositeDisposable = new CompositeDisposable();
    }

    public void start() {
        PhotoRepository photoRepository = new PhotoRepository(this);
        mViewListener = (ViewContractListener) mContext;
        mViewListener.updateProgress(true);
        photoRepository.initPhotoRequest();
    }

    public Observer<PhotoResponse> getPhotoResponseObserver() {
        return new Observer<PhotoResponse>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PhotoResponse response) {
            }

            @Override
            public void onError(Throwable e) {
                mViewListener.updateUI(new ArrayList<Size>());
            }

            @Override
            public void onComplete() {
            }
        };
    }


    public Observer<List<Size>> getPhotoListObserver() {
        return new Observer<List<Size>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onNext(List<Size> dataSet) {
                ArrayList<Size> finalDataSet = new ArrayList<>(dataSet);
                mViewListener.updateUI(finalDataSet);
            }


            @Override
            public void onError(Throwable error) {
                mViewListener.updateUI(new ArrayList<Size>());
            }

            @Override
            public void onComplete() {
            }
        };
    }


    public void stop() {
        mContext = null;
        mViewListener = null;
        mCompositeDisposable.clear();
    }

    public interface ViewContractListener {
        void updateProgress(boolean shouldDisplay);

        void updateUI(List<Size> dataSet);
    }


}