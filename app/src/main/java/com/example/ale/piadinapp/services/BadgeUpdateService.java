package com.example.ale.piadinapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.ale.piadinapp.BadgeActivity;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.SessionManager;
import com.example.ale.utility.VolleyCallback;
import com.example.ale.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BadgeUpdateService extends IntentService {
    private static  final String URL_GET_BADGE = "http://piadinapp.altervista.org/get_timbri.php";
    private static  final char SEPARATOR = ';';

    private VolleyCallback serviceCallback;

    public BadgeUpdateService()
    {
        super(BadgeUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        serviceCallback = new VolleyCallback() {
            @Override
            public void onSuccess(String result)
            {
                Log.d("BADGE_CALLBACK",result);
            }

            @Override
            public void onSuccessMap(int duration) {}
        };

        HashMap<String,String> userData = SessionManager.getUserDetails(getApplicationContext());
        getBadgeDataRequest(userData.get("email"));
    }

    @Override
    public void onDestroy()
    {
        Log.d("BADGE_SERVICE","Distruzione service");
    }

    public void stopBadgeUpdateService()
    {
        //Intent intent = new Intent(BadgeActivity.geta)
    }

    //PRIVATE FUNCTIONS----------------------------------------------------------
    private void getBadgeDataRequest(String userEmail)
    {
        Map<String,String> params = new HashMap<>();
        params.put("email",userEmail);

        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST, URL_GET_BADGE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("BADGE/UPDATE", response.toString());

                        try {
                            int success = response.getInt("success");

                            if(success == 1) {
                                JSONArray timbro = response.getJSONArray("timbro");
                                JSONObject obj = timbro.getJSONObject(0);
                                String numeroTimbriStr = obj.getString("numero_timbri");
                                String omaggiRicevutiStr = obj.getString("omaggi_ricevuti");

                                serviceCallback.onSuccess(numeroTimbriStr + SEPARATOR + omaggiRicevutiStr);
                            } else {
                                Log.d("BADGE_SERVICE/ERROR","Richiesta fallita");
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError){
                            Toast.makeText(getApplicationContext(), "TimeOut Error!", Toast.LENGTH_SHORT).show();
                        }else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "NoConnection Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
    }
    //---------------------------------------------------------------------------
}
