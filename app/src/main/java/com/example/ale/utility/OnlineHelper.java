package com.example.ale.utility;

import android.content.Context;
import android.support.annotation.NonNull;
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

import com.example.ale.piadinapp.classi.Ordine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OnlineHelper {

    String urlCreaOrdine = "http://piadinapp.altervista.org/create_order.php";
    public OnlineHelper(){

    }

    public void addOrderinExternalDB(final Context context, Ordine ordine){
        /*JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();*/
        Map<String, String> params = new HashMap();

        String emailOrdine = ordine.getEmailUtente();
        String telefonoOrdine = ordine.getTelefonoUtente();
        String dataOrdine = ordine.getTimestampOrdine();
        double totaleOrdine = ordine.getPrezzoOrdine();
        String descrizioneOrdine = ordine.printPiadine();
        String notaOrdine = ordine.getNotaOrdine();

        try{
            params.put("email", emailOrdine);
            params.put("phone", telefonoOrdine);
            params.put("data_ordine", dataOrdine);
            params.put("descrizione", descrizioneOrdine);
            params.put("nota", notaOrdine);
            params.put("prezzo", String.valueOf(totaleOrdine));

        }catch(Exception e){
            e.fillInStackTrace();
        }

        //JSONObject parameters = new JSONObject(params);
        //Log.d("JSON", parameters.toString());


        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, urlCreaOrdine, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());
                        try{
                            String success = response.getString("success");
                            Log.d("JSON", "success: " + success);
                            if(success.equals("1")){
                                Toast.makeText(context, "Ordine effettuato!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Oh no :(", Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e){
                            e.fillInStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError){
                    Toast.makeText(context, "TimeOut Error!", Toast.LENGTH_SHORT).show();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "NoConnection Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(context, "Parse Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
    }
}