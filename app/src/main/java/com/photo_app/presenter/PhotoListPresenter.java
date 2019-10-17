package com.photo_app.presenter;

import android.content.Context;

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
    private final CompositeDisposable compositeDisposable;


    public PhotoListPresenter(Context context) {
        mContext = context;
        compositeDisposable = new CompositeDisposable();
    }

    public void start() {
        PhotoRepository photoRepository = new PhotoRepository(getObserver());
        mViewListener = (ViewContractListener) mContext;
        mViewListener.updateProgress(true);
        photoRepository.initPhotoRequest();
    }


    private Observer<List<Size>> getObserver() {
        return new Observer<List<Size>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(List<Size> dataSet) {
                ArrayList<Size> finalDataSet = new ArrayList<>(dataSet);
                mViewListener.updateUI(finalDataSet);
                mViewListener.updateProgress(false);
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
        compositeDisposable.clear();
    }

    public interface ViewContractListener {
        void updateProgress(boolean shouldDisplay);

        void updateUI(List<Size> dataSet);
    }


}