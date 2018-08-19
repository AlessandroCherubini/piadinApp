package com.example.ale.piadinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.VolleyCallback;
import com.example.ale.utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity{

    DBHelper helper;

    VolleyCallback piadineCallBack;
    VolleyCallback ingredientiCallBack;

    final String urlGetPiadine = "http://piadinapp.altervista.org/get_all_piadine.php";
    final String urlGetIngredienti = "http://piadinapp.altervista.org/get_all_ingredients.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        helper = new DBHelper(this);


        piadineCallBack = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                ingredientiCallBack = new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onSuccessMap(int duration) {

                    }
                };
                checkUpdateIngredienti();
            }

            @Override
            public void onSuccessMap(int duration) {

            }
        };
        checkUpdatePiadine();
    }

    public void checkUpdatePiadine() {
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, urlGetPiadine, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray piadine = response.getJSONArray("piadine");
                            long serverTimeStamp = response.getLong("timestamp");

                            long internalLastUpdateTimeStamp = helper.getInternalTimeStampPiadine();
                            long diff = serverTimeStamp-internalLastUpdateTimeStamp;

                            if (Math.abs(diff) > 0) {
                                for (int i = 0; i < piadine.length(); i++) {
                                    JSONObject piadina = piadine.getJSONObject(i);
                                    long idPiadina = piadina.getLong("id_piadina");
                                    String nomePiadina = piadina.getString("nome");
                                    String descrizionePiadina = piadina.getString("descrizione");
                                    Double prezzoPiadina = piadina.getDouble("prezzo");
                                    String formatoPiadina = piadina.getString("formato");
                                    String impastoPiadina = piadina.getString("impasto");
                                    int quantitaPiadina = piadina.getInt("quantita");
                                    int ratingPiadina = piadina.getInt("rating");

                                    ArrayList<Ingrediente> ingredientiPiadina = helper.getIngredientiFromString(descrizionePiadina);

                                    Piadina piadinaInterna = new Piadina(idPiadina, nomePiadina, ingredientiPiadina, prezzoPiadina,
                                            formatoPiadina, impastoPiadina, quantitaPiadina, ratingPiadina, serverTimeStamp);
                                    helper.insertPiadina(piadinaInterna);
                                }

                            } else {
                                Log.d("DB", "Stessa versione del DB, non aggiorno!");

                            }

                            piadineCallBack.onSuccess("Ok");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, "Errore nella richiesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Connessione troppo lenta: non sono riuscito a scaricare i dati", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Nessuna connessione ad Internet!", Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    public void checkUpdateIngredienti() {
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, urlGetIngredienti, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ingredienti = response.getJSONArray("ingredienti");
                            long serverTimeStamp = response.getLong("timestamp");

                            long internalLastUpdateTimeStamp = helper.getInternalTimeStampIngredienti();
                            long diff = serverTimeStamp-internalLastUpdateTimeStamp;

                            if (Math.abs(diff) > 0) {
                                for (int i = 0; i < ingredienti.length(); i++) {
                                    JSONObject ingrediente = ingredienti.getJSONObject(i);
                                    long idIngrediente = ingrediente.getLong("id_ingrediente");
                                    String nomeIngrediente = ingrediente.getString("nome");
                                    Double prezzoIngrediente = ingrediente.getDouble("prezzo");
                                    String allergeniIngrediente = ingrediente.getString("allergeni");
                                    String categoriaIngrediente = ingrediente.getString("categoria");

                                    Ingrediente ingredienteInterno = new Ingrediente(idIngrediente, nomeIngrediente, prezzoIngrediente, allergeniIngrediente,
                                            categoriaIngrediente, serverTimeStamp);
                                    helper.insertIngrediente(ingredienteInterno);
                                }
                            } else {
                                Log.d("DB", "Stessa versione del DB, non aggiorno!");
                            }

                            ingredientiCallBack.onSuccess("Ok");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, "Errore nella richiesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Connessione troppo lenta: non sono riuscito a scaricare i dati", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Nessuna connessione ad Internet!", Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
