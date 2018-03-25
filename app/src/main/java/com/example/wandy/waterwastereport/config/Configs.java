package com.example.wandy.waterwastereport.config;

/**
 * Created by wandy on 10/18/17.
 */

public class Configs {
    public static final Configs instance = new Configs();

    public final String serverURL = "http://35.198.236.190/";
//    public final String serverURL = "http://192.168.100.121/";
    private final String route = serverURL + "api/";

    public final String authorizedValue = "123";
    public final String authorizedKey = "auth";

    public final String upload = route + "post";
    public final String info = route + "info";

}
