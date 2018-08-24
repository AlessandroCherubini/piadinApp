package com.example.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;
import com.example.android.utility.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

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
                String email = JSONHelper.getStringFromArrayObj(resultData,"timbro","email");
                int numeroTimbri = JSONHelper.getIntFromArrayObj(resultData,"timbro","numero_timbri");
                int omaggiRicevuti = JSONHelper.getIntFromArrayObj(resultData,"timbro","omaggi_ricevuti");

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
        };

        HashMap<String,String> userData = SessionManager.getUserDetails(getApplicationContext());
        //getBadgeDataRequest(userData.get("email"));
        OnlineHelper onlineHelper = new OnlineHelper(getApplicationContext());
        onlineHelper.getBadgeDataFromExternalDB(userData.get("email"), badgeCallback);
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
