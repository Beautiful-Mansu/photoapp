package com.photo_app.model.network;

import android.text.TextUtils;

import com.photo_app.model.data.Photo;
import com.photo_app.model.data.PhotoItem;
import com.photo_app.model.data.PhotoResponse;
import com.photo_app.model.data.Size;
import com.photo_app.presenter.PhotoListPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PhotoRepository {


    private static final String LABEL_THUMBNAIL = "Thumbnail";
    private final PhotoListPresenter mPresenter;

    public PhotoRepository(PhotoListPresenter presenter) {
        mPresenter = presenter;
    }

    public void initPhotoRequest() {
        fetchData();
    }


    private void fetchData() {
        Retrofit retrofit = PhotoServiceClient.getRetrofitInstance();
        PhotoService service = retrofit.create(PhotoService.class);
        Observable<PhotoResponse> servicePhotosList = service.getPhotosList();

        servicePhotosList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<PhotoResponse>() {
                    @Override
                    public void accept(PhotoResponse response) throws Exception {
                        if (response != null && response.getPhotos() != null) {
                            List<Photo> photoList = response.getPhotos().getPhoto();
                            makePhotoBatchRequest(photoList);
                        } else {
                            // if response is null & repose has empty list throw exception so that observer onError() will be called.
                            throw new Exception();
                        }
                    }
                })
                .subscribe(mPresenter.getPhotoResponseObserver());
    }


    private void makePhotoBatchRequest(List<Photo> photoList) {
        Retrofit retrofit = PhotoServiceClient.getRetrofitInstance();
        PhotoService service = retrofit.create(PhotoService.class);

        List<Observable<PhotoItem>> observableList = new ArrayList<>();
        for (Photo photo : photoList) {
            Observable<PhotoItem> photoItemCall = service.getPhoto(photo.getId());
            observableList.add(photoItemCall);
        }

        Observable.zip(observableList, new Function<Object[], List<Size>>() {
            @Override
            public List<Size> apply(Object[] objects) throws Exception {
                return filterThumbnails(objects);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mPresenter.getPhotoListObserver());
    }

    private List<Size> filterThumbnails(final Object[] objects) {
        final List<Size> sizesList = new ArrayList<>();
        for (Object item : objects) {
            if (item instanceof PhotoItem) {
                List<Size> sizes = ((PhotoItem) item).getSizes().getSize();
                for (Size size : sizes) {
                    if (TextUtils.equals(size.getLabel(), LABEL_THUMBNAIL)) {
                        sizesList.add(size);
                    }
                }
            }
        }
        return sizesList;
    }

}
