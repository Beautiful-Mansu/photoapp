package com.photo_app.model.network;


import com.photo_app.model.data.PhotoItem;
import com.photo_app.model.data.PhotoResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotoService {

    @GET("services/rest/?method=flickr.people.getPublicPhotos&api_key=227be805b3d6e934926d049533bb938a&user_id=135487628%40N06&format=json&nojsoncallback=1")
    Observable<PhotoResponse> getPhotosList();


    @GET("services/rest/?method=flickr.photos.getSizes&api_key=227be805b3d6e934926d049533bb938a&user_id=135487628%40N06&format=json&nojsoncallback=1")
    Observable<PhotoItem> getPhoto(@Query("photo_id") String photoId);
}