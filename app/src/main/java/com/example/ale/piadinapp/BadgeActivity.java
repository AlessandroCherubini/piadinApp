package com.example.ale.piadinapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.ale.piadinapp.home.ShakerActivity;
import com.example.ale.piadinapp.classi.Timbro;
import com.example.ale.piadinapp.services.BadgeUpdateService;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;
import com.example.ale.utility.VolleySingleton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class BadgeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final char SEPARATOR_QR_STR = ';';
    private static final String URL_GET_BADGE = "http://piadinapp.altervista.org/get_timbri.php";
    private static final int SERVICE_INTERVAL = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ottengo le informazioni dall'utente dalle preferenze condivise e le imposto nella barra.
        final HashMap<String, String> utente;
        //utente = session.getUserDetails();
        utente = SessionManager.getUserDetails(this);

        TextView txtProfileName = navigationView.getHeaderView(0).findViewById(R.id.username_nav);
        txtProfileName.setText(utente.get("name"));

        TextView txtProfileEmail = navigationView.getHeaderView(0).findViewById(R.id.email_nav);
        txtProfileEmail.setText(utente.get("email"));

        //Recupero email dell'utente
        final String userEmail = utente.get("email");
        //todo: recuperare numero di piadine dall'ordine più recente
        String timbriNumberStr = Integer.toString(1);
        //Creazione dell'immagine del QR code
        try {
            BarcodeEncoder qrEncoder = new BarcodeEncoder();
            String msgToEncode = userEmail + SEPARATOR_QR_STR + timbriNumberStr;
            Bitmap qrImage = qrEncoder.encodeBitmap(msgToEncode, BarcodeFormat.QR_CODE,200,200);
            ImageView qrImageView = findViewById(R.id.qrCodeImageView);
            qrImageView.setImageBitmap(qrImage);
        } catch (Exception e) {
            Log.d("Create QR code image",e.getMessage());
        }

        updateBadgeValuesString();

        //Update badge values from DB
        /*
        Button updateBadgeBtn = (Button) findViewById(R.id.btnUpdateBadgeValues);
        updateBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserBadgeExternalDB(userEmail);
            }
        });
        */

        //Start/Stop service button
        final Button startService = (Button) findViewById(R.id.btnStartBadgeService);
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = { Manifest.permission.INTERNET };
                if(EasyPermissions.hasPermissions(BadgeActivity.this,permissions)) {
                    Log.d("PERMESSI","Set Internet permission");
                    startUpdateService(v);
                } else {
                    EasyPermissions.requestPermissions(BadgeActivity.this,"Richiesta permesso per accesso a Internet",1,permissions);
                    startUpdateService(v);
                }
            }
        });

        Button stopService = (Button) findViewById(R.id.btnStopBadgeService);
        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdateService();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.badge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // Click sulla ToolBar: se vogliamo che faccia cose cliccando sulle icone
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {

            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.tessera) {


        } else if (id == R.id.logout) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            final ProgressDialog progressDialog = new ProgressDialog(BadgeActivity.this,
                                    R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Logout in corso...");
                            progressDialog.show();

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // termina la sessione dell'utente.
                                            //session.logoutUser();
                                            SessionManager.logoutUser(getApplicationContext());
                                            finish();
                                            progressDialog.dismiss();

                                        }
                                    }, 2000);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            // Non si fa niente!
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Vuoi veramente uscire da questo account?").setPositiveButton("Sì", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        } else if (id == R.id.call) {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0302122931"));
            startActivity(intent);

        } else if (id == R.id.where) {

            Intent intent = new Intent(this, WeAreHereActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.ordini) {

            Intent intent = new Intent(this, MyOrderActivity.class);
            startActivity(intent);
            finish();

        }else if(id == R.id.shaker){

            Intent intent = new Intent(this, ShakerActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //PRIVATE FUNCTIONS-----------------------------------------------------------
    private String getTimbriStr(int timbriNumber)
    {
        return "Numero di timbri: " + Integer.toString(timbriNumber);
    }

    private String getOmaggiStr(int omaggiNumber)
    {
        return "Piadine omaggio guadagnate: " + Integer.toString(omaggiNumber);
    }

    private void getUserBadgeExternalDB(final String emailUtente) {
        Map<String, String> params = new HashMap();
        params.put("email", emailUtente);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, URL_GET_BADGE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("BADGE/CONTROLLO_UTENTE", response.toString());
                        try {
                            int success = response.getInt("success");

                            if(success == 1) {
                                JSONArray timbro = response.getJSONArray("timbro");
                                JSONObject obj = timbro.getJSONObject(0);
                                int numeroTimbri = obj.getInt("numero_timbri");
                                int omaggiRicevuti = obj.getInt("omaggi_ricevuti");

                                //Timbro newTimbro = new Timbro(0, emailUtente, numeroTimbri, omaggiRicevuti);
                                DBHelper helper = new DBHelper(BadgeActivity.this);
                                boolean res = helper.updateTimbroByEmail(emailUtente,numeroTimbri,omaggiRicevuti);
                                helper.printTimbriTable();
                                if(res) {
                                    SessionManager.updateTimbriAndOmaggiValues(BadgeActivity.this,numeroTimbri,omaggiRicevuti);
                                    Toast.makeText(BadgeActivity.this,"Tessera aggiornata!",Toast.LENGTH_SHORT).show();
                                    updateBadgeValuesString();
                                } else {
                                    Toast.makeText(BadgeActivity.this,"Aggiornamento Tessera fallito!",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(BadgeActivity.this,"Non è stato possibile scaricate i dati della tessera!",Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e){
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

        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void updateBadgeValuesString()
    {
        TextView timbriTV = findViewById(R.id.timbriTextView);
        TextView omaggiTV = findViewById(R.id.omaggiTextView);

        //Get badge infos
        final HashMap<String,Integer> badgeData;
        badgeData = SessionManager.getBadgeDetails(this);
        if(badgeData == null) {
            Log.d("Shared Data Error", "Cannot get Badge shared prefs");
            timbriTV.setText(getTimbriStr(0));
            omaggiTV.setText(getOmaggiStr(0));
        } else {
            //Inserimento numero di timbri
            timbriTV.setText(getTimbriStr(badgeData.get("timbri")));
            //Inserimento numero di omaggi
            omaggiTV.setText(getOmaggiStr(badgeData.get("omaggi")));
        }
    }

    private void startUpdateService(View v)
    {
        Log.d("START","SERVICE: Update badge service");
        startService(new Intent(this, BadgeUpdateService.class));
        Intent badgeUpdateIntent = new Intent(this,BadgeUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,badgeUpdateIntent,0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
         * Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
         * SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
         */
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),SERVICE_INTERVAL,pendingIntent);
    }

    private void stopUpdateService()
    {
        Intent intent = new Intent(this,BadgeUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),0,intent,0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        stopService(intent);
        pendingIntent.cancel();
        alarm.cancel(pendingIntent);
        stopService(new Intent(getApplicationContext(),BadgeUpdateService.class));
        Log.d("SERVICE","Update Badge stopped");
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
