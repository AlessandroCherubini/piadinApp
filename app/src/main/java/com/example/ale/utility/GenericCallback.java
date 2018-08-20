package com.example.ale.utility;

import org.json.JSONObject;

public interface GenericCallback {
    void onSuccess(JSONObject resultData);

    void onFail(String errorStr);
}
