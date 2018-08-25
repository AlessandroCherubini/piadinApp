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

import com.example.android.classi.Ordine;
import com.example.android.classi.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OnlineHelper {

    private static final String URL_GET_USER = "http://piadinapp.altervista.org/get_user.php";
    private static final String URL_MANAGE_ORDER = "http://piadinapp.altervista.org/create_manage_order.php";
    private static final String URL_GET_USER_ORDERS = "http://piadinapp.altervista.org/get_all_orders.php";
    private static final String URL_GET_USER_PIADINE = "http://piadinapp.altervista.org/get_le_mie_piadine.php";
    private static final String URL_CREA_ORDINE = "http://piadinapp.altervista.org/create_order.php";
    private static final String URL_GET_BADGE = "http://piadinapp.altervista.org/get_timbri.php";
    private static final String URL_UPDATE_USER = "http://piadinapp.altervista.org/update_user_jimmy.php"; //todo cambiare quando verifico che funziona

    //Update user fields
    private static final String ID_USER_FIELD       = "user_id";
    private static final String NAME_USER_FIELD     = "name";
    private static final String PASSWORD_USER_FIELD = "password";
    private static final String EMAIL_USER_FIELD    = "email";
    private static final String PHONE_USER_FIELD    = "phone";

    private Response.ErrorListener errorListener;
    private Context mContext;

    public OnlineHelper(final Context context)
    {
        mContext = context;

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    Toast.makeText(mContext, "TimeOut Error!", Toast.LENGTH_SHORT).show();
                }else if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext, "NoConnection Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(mContext, "Authentication Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mContext, "Server Side Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(mContext, "Parse Error!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void addUserOrder(final Ordine ordine, final GenericCallback callback){
        String emailOrdine = ordine.getEmailUtente();
        String telefonoOrdine = ordine.getTelefonoUtente();
        String dataOrdine = ordine.getTimestampOrdine();
        double totaleOrdine = ordine.getPrezzoOrdine();
        String descrizioneOrdine = ordine.printPiadine();
        String notaOrdine = ordine.getNotaOrdine();

        Map<String, String> params = new HashMap<>();
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

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST,
                URL_CREA_ORDINE,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public void addManageOrder(final Ordine ordine, final String dataOrdine, final int idFascia,
                               final int quantitaPiadine, final String emailUtente, final GenericCallback callback)
    {
        Map<String, String> params = new HashMap<>();
        try{
            params.put("data_ordine", dataOrdine);
            params.put("id_fascia", String.valueOf(idFascia));
            params.put("quantita", String.valueOf(quantitaPiadine));
            params.put("email", emailUtente);
        }catch(Exception e){
            e.fillInStackTrace();
        }

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST,
                URL_MANAGE_ORDER,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public void getUserData(final String email,final GenericCallback callback)
    {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST,
                URL_GET_USER,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonRequest);
    }

    public void getUserOrders(final String emailUtente, final GenericCallback callback)
    {
        Map<String, String> params = new HashMap<>();
        params.put("email", emailUtente);

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST,
                URL_GET_USER_ORDERS,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public void getUserPiadine(final String emailUtente, final GenericCallback callback)
    {
        Map<String, String> params = new HashMap<>();
        params.put("email", emailUtente);

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST,
                URL_GET_USER_PIADINE,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    /*public void addOrderInExternalDB(Ordine ordine, final GenericCallback callback)
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
                        Toast.makeText(mContext, "Ordine effettuato!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(mContext, "Oh no :(", Toast.LENGTH_SHORT).show();
                    }

                    if(callback != null)
                        callback.onSuccess(response);
                }catch(JSONException e){
                    e.fillInStackTrace();
                }
            }
        };

        //Create request
        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST,
                URL_CREA_ORDINE,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonRequest);
    }*/

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

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonRequest);
    }

    public void updateUserInExternalDB(User userData, final GenericCallback callback)
    {
        Map<String,String> params = new HashMap<>();
        params.put(ID_USER_FIELD,String.valueOf(userData.userId));
        params.put(NAME_USER_FIELD,userData.nickname);
        params.put(PASSWORD_USER_FIELD,userData.password);
        params.put(EMAIL_USER_FIELD,userData.email);
        params.put(PHONE_USER_FIELD,userData.phone);

        //Create response listener
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        };

        //Create request
        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST,
                URL_UPDATE_USER,
                params,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonRequest);
    }
}