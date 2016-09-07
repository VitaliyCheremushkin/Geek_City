package com.redvord.geekcity.Model;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.Callback;
import retrofit.http.Headers;
import retrofit.http.QueryMap;

/**
 * Created by redvo on 19.01.2016.
 */
public interface NewsService {

    @Headers("Cache-Control: no-cache")
    @GET("/posts")
    void getPostsForPage(@QueryMap Map<String,String> filters,Callback<List<News>> callback);

}
