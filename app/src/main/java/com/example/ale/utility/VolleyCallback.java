package com.example.ale.utility;

import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(String result);

    void onSuccessMap(int duration);
}