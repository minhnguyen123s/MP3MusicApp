package com.example.android.mp3musicapp.Service;

public class ApiService {
    private static String base_url ="https://mp3amnhac.000webhostapp.com/Server/";

    public static DataService getService(){
        return ApiRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
