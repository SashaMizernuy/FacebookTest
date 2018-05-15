package ua.genesis.sasha.facebooktest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PlaceHolderApi {

    @GET("users")
    Call<List<MainModel>> users();
    Call<List<Geo>> geoUsers();

}
