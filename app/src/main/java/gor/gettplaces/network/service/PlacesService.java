package gor.gettplaces.network.service;


import gor.gettplaces.network.pojo.NearBySearchResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {
    final String SERVICE_ENDPOINT = "https://maps.googleapis.com";
    final int DEFAULT_RADIUS = 1000;

    ///maps/api/place/nearbysearch/json?key=AIzaSyAszlCnuj9J0pq9umJSl7ANHOIBJkF9l4w?location=32.0718355,34.7794304?radius=500

    @GET("/maps/api/place/nearbysearch/json")//?key=AIzaSyAszlCnuj9J0pq9umJSl7ANHOIBJkF9l4w&location=32.0718355,34.7794304&radius=500")
//    Observable<NearBySearchResponse> getNearByPlaces();
    Observable<NearBySearchResponse> getNearByPlaces(@Query("key") String key,
                                                     @Query("location") String location,
                                                     @Query("radius") int radius);
}
