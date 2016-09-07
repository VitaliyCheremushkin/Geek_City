package com.redvord.geekcity.Model;

/**
 * Created by redvo on 19.01.2016.
 */
public class RestNews {
    private static final String URL = "http://geekcity.ru/wp-json/";
    private retrofit.RestAdapter restAdapter;
    private NewsService newsService;

    public RestNews()
    {

        restAdapter = new retrofit.RestAdapter.Builder()
                .setEndpoint(URL)
                .setLogLevel(retrofit.RestAdapter.LogLevel.FULL)
                .build();

        newsService = restAdapter.create(NewsService.class);
    }

    public NewsService getService()
    {
        return newsService;
    }
}
