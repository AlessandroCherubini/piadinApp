package com.example.ale.piadinapp;

import android.content.Context;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    //String urlVersione = "http://piadinapp.altervista.org/get_db_version.php";
    Context mContext;
    private RequestQueue queue;
    DBHelper helper;

    final String urlGetPiadine = "http://piadinapp.altervista.org/get_all_piadine.php";
    final String urlGetIngredienti = "http://piadinapp.altervista.org/get_all_ingredients.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        queue = Volley.newRequestQueue(this);
        helper = new DBHelper(this);

        checkUpdatePiadine();
        checkUpdateIngredienti();

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();

        /*// Find the progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // Start your loading
        new LoadingTask(progressBar, this).execute("");*/

    }

    /*@Override
    public void onTaskFinished() {
        completeSplash();
    }

    private void completeSplash(){
        startApp();
        finish(); // Don't forget to finish this Splash Activity so the user can't return to it!
    }

    private void startApp() {
        // redirect alla pagina principale
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }*/

    public void checkUpdatePiadine() {
        Log.d("DB", "Controllo il timestamp della tabella piadine sul DB esterno");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, urlGetPiadine, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Database", response.toString());
                        try {
                            JSONArray piadine = response.getJSONArray("piadine");
                            long serverTimeStamp = response.getLong("timestamp");

                            Log.d("DB", "Timestamp db esterno: "+serverTimeStamp);

                            long internalLastUpdateTimeStamp = helper.getInternalTimeStampPiadine();
                            long diff = serverTimeStamp-internalLastUpdateTimeStamp;

                            if (Math.abs(diff) > 0) {
                                Log.d("DB", "Timestamp db interno: "+internalLastUpdateTimeStamp);
                                //Toast.makeText(SplashActivity.this, "Aggiorno le tabelle interne", Toast.LENGTH_SHORT).show();

                                Toast.makeText(SplashActivity.this, " "+diff, Toast.LENGTH_LONG).show();

                                // TODO: da mettere nel db interno i dati scaricati dal db esterno.

                                for (int i = 0; i < piadine.length(); i++) {
                                    JSONObject piadina = piadine.getJSONObject(i);
                                    long idPiadina = piadina.getLong("id_piadina");
                                    String nomePiadina = piadina.getString("nome");
                                    String descrizionePiadina = piadina.getString("descrizione");
                                    Double prezzoPiadina = piadina.getDouble("prezzo");

                                    ArrayList<Ingrediente> ingredientiPiadina = helper.getIngredientiFromString(descrizionePiadina);

                                    Piadina piadinaInterna = new Piadina(idPiadina, nomePiadina, ingredientiPiadina, prezzoPiadina, serverTimeStamp);
                                    helper.insertPiadina(piadinaInterna);
                                }
                                Log.d("DB/INSERT", "Tutte le piadine sono state aggiornate");

                                helper.printPiadineTable();

                            } else {
                                Log.d("DB", "Stessa versione del DB, non aggiorno!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, "Errore nella richiesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "TimeOut Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
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

        queue.add(jsObjRequest);
    }

    public void checkUpdateIngredienti() {
        Log.d("DB", "Controllo il timestamp della tabella ingredienti sul DB esterno");
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.GET, urlGetIngredienti, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Database", response.toString());
                        try {
                            JSONArray ingredienti = response.getJSONArray("ingredienti");
                            long serverTimeStamp = response.getLong("timestamp");

                            Log.d("DB", "Timestamp db esterno: "+serverTimeStamp);

                            long internalLastUpdateTimeStamp = helper.getInternalTimeStampIngredienti();
                            long diff = serverTimeStamp-internalLastUpdateTimeStamp;

                            if (Math.abs(diff) > 0) {
                                Log.d("DB", "Timestamp db interno: "+internalLastUpdateTimeStamp);
                                //Toast.makeText(SplashActivity.this, "Aggiorno le tabelle interne", Toast.LENGTH_SHORT).show();

                                Toast.makeText(SplashActivity.this, " "+diff, Toast.LENGTH_LONG).show();

                                // TODO: da mettere nel db interno i dati scaricati dal db esterno.

                                for (int i = 0; i < ingredienti.length(); i++) {
                                    JSONObject ingrediente = ingredienti.getJSONObject(i);
                                    long idIngrediente = ingrediente.getLong("id_ingrediente");
                                    String nomeIngrediente = ingrediente.getString("nome");
                                    Double prezzoIngrediente = ingrediente.getDouble("prezzo");
                                    String allergeniIngrediente = ingrediente.getString("allergeni");
                                    Log.d("ALLERGENI", allergeniIngrediente);

                                    Ingrediente ingredienteInterno = new Ingrediente(idIngrediente, nomeIngrediente, prezzoIngrediente, allergeniIngrediente, serverTimeStamp);
                                    helper.insertIngrediente(ingredienteInterno);
                                }
                                Log.d("DB/INSERT", "Tutte gli ingredienti sono stati aggiornati");

                                helper.printIngredientiTable();

                            } else {
                                Log.d("DB", "Stessa versione del DB, non aggiorno!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, "Errore nella richiesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "TimeOut Error!", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
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

        queue.add(jsObjRequest);
    }

}
