package com.example.ale.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
Classe utilizzata per estrapolare i dati dai JSONObject risultato delle CustomRequest
 */
public class JSONHelper {

    //Static class
    private JSONHelper() {}

    public static String getStringFromObj(JSONObject source,String arrayName,String key)
    {
        try {
            JSONArray array = source.getJSONArray(arrayName);
            JSONObject obj = array.getJSONObject(0);
            return obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getIntFromObj(JSONObject source,String arrayName,String key)
    {
        try {
            JSONArray array = source.getJSONArray(arrayName);
            JSONObject obj = array.getJSONObject(0);
            return obj.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
