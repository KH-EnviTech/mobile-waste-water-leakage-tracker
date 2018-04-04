package com.cambodia.od4d.wastewaterleakagetracker.rest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cambodia.od4d.wastewaterleakagetracker.callback.OnRequestStringResult;
import com.cambodia.od4d.wastewaterleakagetracker.config.Configs;
import com.cambodia.od4d.wastewaterleakagetracker.controller.AppController;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by wandy on 10/18/17.
 */

public class NetworkRequestString {

    public NetworkRequestString(Context context,String url, final HashMap<String, String> body, final OnRequestStringResult onRequestStringResult) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                onRequestStringResult.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onRequestStringResult.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> header = new HashMap<>();

                header.put(Configs.instance.authorizedKey, Configs.instance.authorizedValue);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return body;
            }


        };

        Volley.newRequestQueue(context).add(request);

    }

}
