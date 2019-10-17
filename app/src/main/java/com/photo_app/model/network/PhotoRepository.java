package com.photo_app.model.network;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.photo_app.model.data.Photo;
import com.photo_app.model.data.PhotoItem;
import com.photo_app.model.data.PhotoResponse;
import com.photo_app.model.data.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotoRepository {


    private static final String LABEL_THUMBNAIL = "Thumbnail";
    private final Observer<List<Size>> mObserver;

    public PhotoRepository(@NonNull Observer<List<Size>> observer) {
        mObserver = observer;
    }

    public void initPhotoRequest() {
        new PhotoAsyncTask().execute();

    }

    @SuppressLint("StaticFieldLeak")
    private class PhotoAsyncTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("CheckResult")
        @Override
        protected Void doInBackground(Void... voids) {
            fetchData();
            return null;
        }

    }


    private void fetchData() {
        Retrofit retrofit = PhotoServiceClient.getRetrofitInstance();
        PhotoService service = retrofit.create(PhotoService.class);
        Call<PhotoResponse> photosListCall = service.getPhotosList();

        photosListCall.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(@NotNull Call<PhotoResponse> call, @NotNull Response<PhotoResponse> response) {
                if (response.body() != null && response.body().getPhotos() != null) {
                    List<Photo> photoList = new ArrayList<>(response.body().getPhotos().getPhoto());
                    makePhotoBatchRequest(photoList);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PhotoResponse> call, @NotNull Throwable t) {

            }
        });
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
                .subscribe(mObserver);
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
