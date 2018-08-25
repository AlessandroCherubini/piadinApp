package com.example.android.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
Classe utilizzata per estrapolare i dati dai JSONObject risultato delle CustomRequest
 */
public class JSONHelper {

    private static final String SUCCESS_FIELD = "success";
    private static final String MESSAGE_FIELD = "message";

    //Static class
    private JSONHelper() {}

    public static String getStringFromArrayObj(JSONObject source,String arrayName,String key)
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

    public static int getIntFromArrayObj(JSONObject source,String arrayName,String key)
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

    public static boolean getSuccessResponseValue(JSONObject source)
    {
        int res;
        try {
            res = source.getInt(SUCCESS_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        if(res != 0)
            return true;

        return false;
    }

    public static String getStringFromObj(JSONObject source,String field)
    {
        try {
            return source.getString(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static long getLongFromObj(JSONObject source,String field)
    {
        try {
            return source.getLong(field);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
