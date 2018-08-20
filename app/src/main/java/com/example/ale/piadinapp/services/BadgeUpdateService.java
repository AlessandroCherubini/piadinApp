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
import com.example.ale.utility.GenericCallback;
import com.example.ale.utility.JSONHelper;
import com.example.ale.utility.OnlineHelper;
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
    //Usato per filtrare gli intent broadcast
    public static final String BROADCAST_ACTION = "com.example.ale.piadinapp.services.updatebadge";

    private GenericCallback badgeCallback;

    public BadgeUpdateService()
    {
        super(BadgeUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent)
    {
        Log.d("BADGE_SRV","Handle service");
        badgeCallback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData) {
                String email = JSONHelper.getStringFromObj(resultData,"timbro","email");
                int numeroTimbri = JSONHelper.getIntFromObj(resultData,"timbro","numero_timbri");
                int omaggiRicevuti = JSONHelper.getIntFromObj(resultData,"timbro","omaggi_ricevuti");

                Log.d("BADGE_CALLBACK/SUCCESS",email);
                //Recupero shared pref
                HashMap<String,Integer> badgeData = SessionManager.getBadgeDetails(getApplicationContext());
                int timbriLocal = badgeData.get(SessionManager.KEY_TIMBRI);
                int omaggiLocal = badgeData.get(SessionManager.KEY_OMAGGI);

                if(timbriLocal == numeroTimbri && omaggiLocal == omaggiRicevuti) {
                    Log.d("BADGE_CALLBACK/VALUES","Valori uguali, ripeto il service");
                } else {
                    Log.d("BADGE_CALLBACK/VALUES", "Valori diversi, aggiorno i valori");
                    DBHelper helper = new DBHelper(getApplicationContext());
                    helper.updateTimbroByEmail(email, numeroTimbri, omaggiRicevuti);
                    //Aggiorno shared preferences
                    SessionManager.updateTimbriAndOmaggiValues(getApplicationContext(), numeroTimbri, omaggiRicevuti);
                    Intent broadcastIntent = new Intent(BROADCAST_ACTION);
                    sendBroadcast(broadcastIntent);
                }
            }

            @Override
            public void onFail(String errorStr) {
                Log.d("BADGE_CALLBACK/ERROR",errorStr);
            }
        };

        HashMap<String,String> userData = SessionManager.getUserDetails(getApplicationContext());
        //getBadgeDataRequest(userData.get("email"));
        OnlineHelper onlineHelper = new OnlineHelper(getApplicationContext());
        onlineHelper.getBadgeDataFromExternalDB(userData.get("email"),badgeCallback);
    }

    @Override
    public void onDestroy()
    {
        Log.d("BADGE_SRV","Destroy service");
    }

    //CALLBACK INTERFACE---------------------------------------------------------
    private interface BadgeServiceCallback {
        void onSuccess(String email,int timbri,int omaggi);

        void onFail(String result);
    }
    //---------------------------------------------------------------------------
}
