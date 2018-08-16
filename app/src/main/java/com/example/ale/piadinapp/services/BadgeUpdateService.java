package com.example.ale.piadinapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
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
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;
import com.example.ale.utility.VolleyCallback;
import com.example.ale.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BadgeUpdateService extends IntentService {
    private static final String URL_GET_BADGE = "http://piadinapp.altervista.org/get_timbri.php";
    private static final String SEPARATOR = ";";
    private static final int SERVICE_INTERVAL = 10000;

    private BadgeServiceCallback badgeCallback;

    public BadgeUpdateService()
    {
        super(BadgeUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        //todo spostare la callback in badge activity
        badgeCallback = new BadgeServiceCallback() {
            @Override
            public void onSuccess(String email,int timbri, int omaggi)
            {
                Log.d("BADGE_CALLBACK/SUCCESS",email);
                //Recupero shared pref
                HashMap<String,Integer> badgeData = SessionManager.getBadgeDetails(getApplicationContext());
                int timbriLocal = badgeData.get(SessionManager.KEY_TIMBRI);
                int omaggiLocal = badgeData.get(SessionManager.KEY_OMAGGI);

                if(timbriLocal == timbri && omaggiLocal == omaggi) {
                    Log.d("BADGE_CALLBACK/VALUES","Valori uguali, ripeto il service");
                    scheduleNextUpdate();
                } else {
                    Log.d("BADGE_CALLBACK/VALUES","Valori diversi, aggiorno i valori");
                    DBHelper helper = new DBHelper(getApplicationContext());
                    helper.updateTimbroByEmail(email,timbri,omaggi);
                    //Aggiorno shared preferences
                    SessionManager.updateTimbriAndOmaggiValues(getApplicationContext(),timbri,omaggi);
                    //Update stringhe nell'activity badge
                }
            }

            @Override
            public void onFail(String result)
            {
                Log.d("BADGE_CALLBACK/ERROR",result);
            }
        };

        HashMap<String,String> userData = SessionManager.getUserDetails(getApplicationContext());
        getBadgeDataRequest(userData.get("email"));
    }

    @Override
    public void onDestroy()
    {}

    //PRIVATE FUNCTIONS----------------------------------------------------------
    private void getBadgeDataRequest(String userEmail)
    {
        Map<String,String> params = new HashMap<>();
        params.put("email",userEmail);

        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST, URL_GET_BADGE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if(success == 1) {
                                JSONArray timbro = response.getJSONArray("timbro");
                                JSONObject obj = timbro.getJSONObject(0);
                                String email = obj.getString("email");
                                int numeroTimbri = obj.getInt("numero_timbri");
                                int omaggiRicevuti = obj.getInt("omaggi_ricevuti");

                                badgeCallback.onSuccess(email,numeroTimbri,omaggiRicevuti);
                            } else {
                                badgeCallback.onFail(response.toString());
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

    private void scheduleNextUpdate()
    {
        Intent intent = new Intent(this,this.getClass());
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
         * Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
         * SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
         */
        if(pendingIntent != null)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SERVICE_INTERVAL, pendingIntent);
    }
    //---------------------------------------------------------------------------

    //CALLBACK INTERFACE---------------------------------------------------------
    private interface BadgeServiceCallback {
        void onSuccess(String email,int timbri,int omaggi);

        void onFail(String result);
    }
    //---------------------------------------------------------------------------
}
