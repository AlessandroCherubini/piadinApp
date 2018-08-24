package com.example.android.utility;

import android.content.Context;
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

import com.example.android.classi.Ingrediente;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnlineHelper {

    String urlCreaOrdine = "http://piadinapp.altervista.org/create_order.php";
    private static final String URL_MANAGE_ORDER = "http://piadinapp.altervista.org/create_manage_order.php";
    private static final String URL_GET_USER_ORDERS = "http://piadinapp.altervista.org/get_all_orders.php";
    private static final String URL_GET_USER_PIADINE = "http://piadinapp.altervista.org/get_le_mie_piadine.php";

    VolleyCallback volleyCallbackUser;
    VolleyCallback volleyCallbackOrder;
    VolleyCallback volleyCallbackUserOrders;
    VolleyCallback volleyCallbackUserPiadine;

    DBHelper helper;

    public void addUserOrder(final Context context, final Ordine ordine){

        volleyCallbackUser = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                helper = new DBHelper(context);
                helper.insertOrdine(ordine);
            }

            @Override
            public void onSuccessMap(int duration, String orarioRitiro) {

            }
        };

        Map<String, String> params = new HashMap();

        String emailOrdine = ordine.getEmailUtente();
        String telefonoOrdine = ordine.getTelefonoUtente();
        String dataOrdine = ordine.getTimestampOrdine();
        //double totaleOrdine = ordine.getPrezzoOrdine();
        double totaleOrdine = (double)Math.round(ordine.getPrezzoOrdine() * 100d)/100d;
        Log.d("TOTALE", "Cazzo che voglio: " + totaleOrdine);
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

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, urlCreaOrdine, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());
                        try{
                            String success = response.getString("success");
                            String timestamp = response.getString("timestamp");

                            Log.d("JSON", "success: " + success);
                            if(success.equals("1")){
                                Toast.makeText(context, "Ordine effettuato!", Toast.LENGTH_SHORT).show();
                                volleyCallbackUser.onSuccess(timestamp);
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

    public void addManageOrder(final Context context, final Ordine ordine, final String dataOrdine,
                               final int idFascia, final int quantitaPiadine, final String emailUtente){

        volleyCallbackOrder = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ordine.setTimestampOrdine(result);

                addUserOrder(context, ordine);
            }

            @Override
            public void onSuccessMap(int duration, String orarioRitiro) {

            }
        };

        Map<String, String> params = new HashMap();

        try{
            params.put("data_ordine", dataOrdine);
            params.put("id_fascia", String.valueOf(idFascia));
            params.put("quantita", String.valueOf(quantitaPiadine));
            params.put("email", emailUtente);

        }catch(Exception e){
            e.fillInStackTrace();
        }

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL_MANAGE_ORDER, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", response.toString());
                        try{
                            String success = response.getString("success");
                            String timestampFine = response.getString("timestamp_fine");

                            Log.d("JSON", "success: " + success);
                            if(success.equals("1")){
                                volleyCallbackOrder.onSuccess(timestampFine);
                            }
                            else{
                                Toast.makeText(context, "Oh no :(", Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
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

    public void getUserOrders(final Context context, final String emailUtente){
        volleyCallbackUserOrders = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d("JSON", result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ordini = jsonObject.getJSONArray("ordini");

                    helper = new DBHelper(context);

                    for(int i = 0; i < ordini.length(); i++){
                        JSONObject ordine = ordini.getJSONObject(i);
                        String phoneOrdine = ordine.getString("user_phone");
                        String dataOrdine = ordine.getString("data_ordine");
                        double totaleOrdine = ordine.getDouble("prezzo");
                        String descrizioneOrdine = ordine.getString("descrizione");
                        ArrayList<Piadina> piadineOrdine = helper.getPiadineFromDescrizioneOrdine(descrizioneOrdine);

                        String notaOrdine = ordine.getString("nota");
                        long lastUpdateOrdine = ordine.getLong("timestamp");

                        Ordine ordineInterno = new Ordine(0, emailUtente, phoneOrdine, dataOrdine,
                                totaleOrdine, piadineOrdine, notaOrdine, lastUpdateOrdine);

                        helper.insertOrdine(ordineInterno);
                    }
                }catch (JSONException e ){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessMap(int duration, String orarioRitiro) {

            }
        };

        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL_GET_USER_ORDERS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String success = response.getString("success");

                            if(success.equals("1")){
                                volleyCallbackUserOrders.onSuccess(response.toString());
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

    public void getUserPiadine(final Context context, final String emailUtente){
        volleyCallbackUserPiadine = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray piadine = jsonObject.getJSONArray("piadine");

                    helper = new DBHelper(context);
                    long lastUpdatePiadine = jsonObject.getLong("timestamp");

                    for(int i = 0; i < piadine.length(); i++) {
                        JSONObject piadina = piadine.getJSONObject(i);
                        int idEsternoPiadina = piadina.getInt("id_piadina");
                        String emailUtente = piadina.getString("email_utente");
                        String nomePiadina = piadina.getString("nome");
                        String descrizionePiadina = piadina.getString("descrizione");
                        ArrayList<Ingrediente> ingredientiPiadina = helper.getIngredientiFromString(descrizionePiadina);
                        double prezzoPiadina = piadina.getDouble("prezzo");
                        String formatoPiadina = piadina.getString("formato");
                        String impastoPiadina = piadina.getString("impasto");
                        int quantitaPiadina = piadina.getInt("quantita");
                        int votoPiadina = piadina.getInt("voto");

                        Piadina piadinaVotata = new Piadina(0, nomePiadina, formatoPiadina, impastoPiadina, ingredientiPiadina,
                                prezzoPiadina, quantitaPiadina, votoPiadina, idEsternoPiadina, lastUpdatePiadine);

                        helper.insertPiadinaVotata(piadinaVotata, emailUtente);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessMap(int duration, String orarioRitiro) {

            }
        };

        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL_GET_USER_PIADINE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String success = response.getString("success");

                            if(success.equals("1")){
                                volleyCallbackUserPiadine.onSuccess(response.toString());
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
