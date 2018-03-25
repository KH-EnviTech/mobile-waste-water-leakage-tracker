package com.example.wandy.waterwastereport.callback;

import com.android.volley.VolleyError;

/**
 * Created by wandy on 10/18/17.
 */

public interface OnRequestStringResult {
    void onSuccess(String result);
    void onError(VolleyError error);
}
