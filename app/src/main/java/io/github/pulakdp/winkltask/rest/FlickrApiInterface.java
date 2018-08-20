package io.github.pulakdp.winkltask.rest;

import io.github.pulakdp.winkltask.model.PhotoResponse;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Author: PulakDebasish
 */

public interface FlickrApiInterface {
    @GET("/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1&sort=relevance")
    Observable<PhotoResponse> searchPhotos(@Query("api_key") String apiKey,
                                           @Query("tags") String query,
                                           @Query("per_page") int perPage);
}
