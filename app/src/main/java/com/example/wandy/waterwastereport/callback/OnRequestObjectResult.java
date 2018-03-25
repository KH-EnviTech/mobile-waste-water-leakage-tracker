package com.example.wandy.waterwastereport.callback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface OnRequestObjectResult {
    void onSuccess(JSONObject result);
    void onError(VolleyError error);
}
