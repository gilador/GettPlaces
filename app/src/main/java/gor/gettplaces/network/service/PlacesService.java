package gor.gettplaces.network.service;


import gor.gettplaces.network.pojo.address.AddressLookUpResponse;
import gor.gettplaces.network.pojo.geoLocation.GeoLocationRespone;
import gor.gettplaces.network.pojo.places.NearBySearchResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {
    String SERVICE_ENDPOINT = "https://maps.googleapis.com";
    int DEFAULT_RADIUS = 1000;

    @GET("/maps/api/place/nearbysearch/json")
    Observable<NearBySearchResponse> getNearByPlaces(@Query("key") String key,
                                                     @Query("location") String location,
                                                     @Query("radius") int radius);

    //https://maps.googleapis.com/maps/api/place/autocomplete/xml?input=Amoeba&types=establishment&location=37.76999,-122.44696&radius=500&key=AIzaSyAszlCnuj9J0pq9umJSl7ANHOIBJkF9l4w
    @GET("/maps/api/place/autocomplete/json")
    Observable<AddressLookUpResponse> getAddress(@Query("key") String key,
                                                 @Query("input") String location);

    @GET("/maps/api/geocode/json")
    Observable<GeoLocationRespone> getGeo(@Query("key") String key,
                                          @Query("address") String address);
}
