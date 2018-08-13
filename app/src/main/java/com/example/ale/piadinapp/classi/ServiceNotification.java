package com.example.ale.piadinapp.classi;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.example.ale.piadinapp.MyOrderActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.home.CartActivity;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.VolleyCallback;
import com.example.ale.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class ServiceNotification extends IntentService implements LocationListener{

    VolleyCallback durataCallBack;


    public ServiceNotification(){

        super("ServiceNotification");
    }


    @Override
    protected void onHandleIntent(Intent i)
    {
        durataCallBack = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {}

            @Override
            public void onSuccessMap(int duration) {

                Log.d("DISTANZA",""+duration);

                GregorianCalendar now = new GregorianCalendar();
                Date oraAttuale = now.getTime();

                GregorianCalendar oraRitiro= new GregorianCalendar(2018, Calendar.AUGUST,13,21,20,00);
                Date oraRitiroDate = oraRitiro.getTime();

                long timeBeforePick = TimeUnit.MILLISECONDS.toSeconds(oraRitiroDate.getTime() - oraAttuale.getTime());


                //TODO IDEARE BENE LE CONDIZIONI DEL METODO
                if (timeBeforePick-((long)duration+TimeUnit.MINUTES.toSeconds(5))<=0){

                    sendNotification();

                    stopNotificationService();

                }

            }
        };

        Location location = getLocation();
        final double latitude =location.getLatitude();
        final double longitude =location.getLongitude();
        travelTimeRequest(latitude,longitude);
    }

    @Override
    public void onDestroy()
    {
        Log.i("PROVA SERVICE", "Distruzione Service");
    }

    public void stopNotificationService(){

        Intent intent = new Intent(CartActivity.getAppContext(), ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        stopService(intent);
        pintent.cancel();
        alarm.cancel(pintent);
        stopService(new Intent(getApplicationContext(),ServiceNotification.class));
    }

    private void sendNotification(){

        Uri gmmIntentUri = Uri.parse("google.navigation:q=GianGusto+Piadineria,+Via+dei+Pioppi,+18,+25080+Molinetto+BS");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent navigatePendingIntent = PendingIntent.getActivity(
                this, 0, mapIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        Intent notificationIntent = new Intent(getApplicationContext(), MyOrderActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent orderIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channel_piadina");
        notification.setSmallIcon(R.mipmap.ic_launcher_round);
        notification.setContentTitle("Ritira l'ordine!");
        notification.setContentText("Se vuoi ritirare lordine in orario devi partire a breve");
        notification.setAutoCancel(true);
        notification.setContentIntent(orderIntent);
        notification.addAction(R.drawable.ic_map_black_24dp, "Naviga", navigatePendingIntent).setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_piadina",
                    "Channel title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification.build());
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(){

        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,100,this);
            lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 100, this);
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, this);
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        return finalLoc;
    }

    public void travelTimeRequest(double latitude, double longitude){


        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+latitude+","+longitude+"&destinations=GianGusto+Piadineria,+Via+dei+Pioppi,+18,+25080+Molinetto+BS/@45.496155,10.3505613,17z&key=AIzaSyADSK-zR8PLVu3ZhD1UOI6S6dcB-BSKjnQ";

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());

                        try {
                            JSONArray rowsArray = response.getJSONArray("rows");
                            JSONObject rows = rowsArray.getJSONObject(0);
                            JSONArray elements = rows.getJSONArray("elements");
                            JSONObject leg = elements.getJSONObject(0);
                            JSONObject durationObject = leg.getJSONObject("duration");
                            int duration = durationObject.getInt("value");

                            durataCallBack.onSuccessMap(duration);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

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
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}