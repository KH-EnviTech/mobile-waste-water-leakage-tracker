package com.example.wandy.waterwastereport.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.wandy.waterwastereport.callback.OnRequestObjectResult;
import com.example.wandy.waterwastereport.config.Configs;
import com.example.wandy.waterwastereport.controller.AppController;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wandy on 10/18/17.
 */

public class NetworkRequestObject {

    public NetworkRequestObject(String url, final JSONObject object, final OnRequestObjectResult onRequestObjectResult){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onRequestObjectResult.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onRequestObjectResult.onError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put(Configs.instance.authorizedKey, Configs.instance.authorizedValue);
                return header;
            }
        };

        AppController.getInstance().addToRequestQueue(request);
    }
}
