package com.example.ale.utility;

import android.app.VoiceInteractor;
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

import java.net.ResponseCache;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OnlineHelper {

    private static final String URL_CREA_ORDINE = "http://piadinapp.altervista.org/create_order.php";
    private static final String URL_GET_BADGE = "http://piadinapp.altervista.org/get_timbri.php";

    private Response.ErrorListener errorListener;
    private Context m_context;

    public OnlineHelper(Context context)
    {
        m_context = context;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(m_context, "TimeOut Error!", Toast.LENGTH_SHORT).show();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(m_context, "NoConnection Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(m_context, "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(m_context, "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(m_context, "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(m_context, "Parse Error!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void addOrderInExternalDB(Ordine ordine, final GenericCallback callback)
    {
        //Fill data
        Map<String,String> params = new HashMap<>();
        String email       = ordine.getEmailUtente();
        String telefono    = ordine.getTelefonoUtente();
        String data        = ordine.getTimestampOrdine();
        double totale      = ordine.getPrezzoOrdine();
        String descrizione = ordine.printPiadine();
        String nota        = ordine.getNotaOrdine();

        try {
            params.put("email",email);
            params.put("phone",telefono);
            params.put("data_ordine",data);
            params.put("descrizione",descrizione);
            params.put("nota",nota);
            params.put("prezzo",String.valueOf(totale));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON", response.toString());
                try{
                    String success = response.getString("success");
                    Log.d("JSON", "success: " + success);
                    if(success.equals("1")){
                        Toast.makeText(m_context, "Ordine effettuato!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(m_context, "Oh no :(", Toast.LENGTH_SHORT).show();
                    }

                    if(callback != null)
                        callback.onSuccess(response);
                }catch(JSONException e){
                    e.fillInStackTrace();
                    if(callback != null)
                        callback.onFail(e.toString());
                }
            }
        };

        //Create request
        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST,
                                                      URL_CREA_ORDINE,
                                                      params,
                                                      responseListener,
                                                      errorListener);

        VolleySingleton.getInstance(m_context).addToRequestQueue(jsonRequest);
    }

    public void getBadgeDataFromExternalDB(String userEmail, final GenericCallback callback)
    {
        Map<String,String> params = new HashMap<>();
        params.put("email",userEmail);

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        //Create request
        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST,
                                                      URL_GET_BADGE,
                                                      params,
                                                      responseListener,
                                                      errorListener);

        VolleySingleton.getInstance(m_context).addToRequestQueue(jsonRequest);
    }

    /*
    String urlCreaOrdine = "http://piadinapp.altervista.org/create_order.php";
    public OnlineHelper(){

    }

    public void addOrderinExternalDB(final Context context, Ordine ordine){
        /*JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();*/

    /*
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
    */
}
