package com.example.android.activity;

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

import com.example.android.classi.Timbro;
import com.example.android.R;
import com.example.android.utility.DBHelper;
import com.example.android.utility.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

public class BadgeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        String userEmail = utente.get("email");
        //Creazione dell'immagine del QR code
        try {
            BarcodeEncoder qrEncoder = new BarcodeEncoder();
            Bitmap qrImage = qrEncoder.encodeBitmap(userEmail, BarcodeFormat.QR_CODE,200,200);
            ImageView qrImageView = findViewById(R.id.qrCodeImageView);
            qrImageView.setImageBitmap(qrImage);
        } catch (Exception e) {
            Log.d("Create QR code image",e.getMessage());
        }

        TextView timbriTV = findViewById(R.id.timbriTextView);
        TextView omaggiTV = findViewById(R.id.omaggiTextView);
        ProgressBar badgePB = findViewById(R.id.omaggioProgressBar);
        badgePB.setMax(10);//todo: inserire costante valore max di timbri
        int timbriNum = 0;

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
            //Update progress bar
            timbriNum = badgeData.get("timbri");
        }

        badgePB.setProgress(timbriNum);
        //Percentage string
        TextView percentText = findViewById(R.id.percentageTextView);
        percentText.setText(Integer.toString(timbriNum * 10) + "%");


        //Bottone per passare all'Activity per leggere il QR code
        Button readQRBtn = findViewById(R.id.readQRBtn);
        readQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start scanning QR code
                beginScanQRCode();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result == null) {
            super.onActivityResult(requestCode,resultCode,data);
        } else {
            if(result.getContents() == null) {
                Log.d("READQR","Scan action cancelled");
            } else {
                //Update timbri and omaggi values
                //Get shared pref
                /*
                session = new SessionManager(this);
                final HashMap<String,Integer> badgeData;
                badgeData = session.getBadgeDetails();

                int timbriNumber = badgeData.get("timbri");
                int omaggiNumber = badgeData.get("omaggi");
                */
                SharedPreferences preferences = getSharedPreferences("piadinApp",Context.MODE_PRIVATE);
                int timbriNumber = preferences.getInt(SessionManager.KEY_TIMBRI,-1);
                int omaggiNumber = preferences.getInt(SessionManager.KEY_OMAGGI,-1);
                if(timbriNumber < 0 || omaggiNumber < 0) {
                    Log.d("READQR","Cannot retrieve shared preferences");
                    return;
                }
                //todo: inserire costante valore max di timbri
                //todo: manca inserimento numero piadine in base all'ordine
                timbriNumber++;
                if(timbriNumber >= 10) {
                    omaggiNumber++;
                    timbriNumber = timbriNumber - 10;
                }

                //Insert new values in DB
                DBHelper helper = new DBHelper(getApplicationContext());
                Timbro timbro = helper.getTimbroByEmail(result.getContents());
                if(timbro == null) {
                    Log.d("UPDATE_TIMBRI","No row found in table Timbri with mail: " + result.getContents());
                    return;
                }
                timbro.numberTimbri = timbriNumber;
                timbro.numberOmaggi = omaggiNumber;
                helper.updateTimbriNumber(timbro);

                //Insert new data in shared pref
                if(!SessionManager.updateTimbriAndOmaggiValues(this,timbriNumber,omaggiNumber)) {
                    Log.d("Shared Pref Error","Failed to save timbri and omaggi values!");
                }

                //Update labels strings
                TextView badgeText = findViewById(R.id.timbriTextView);
                badgeText.setText(getTimbriStr(timbriNumber));
                badgeText = findViewById(R.id.omaggiTextView);
                badgeText.setText(getOmaggiStr(omaggiNumber));

                //Update progress bar
                ProgressBar badgePB = findViewById(R.id.omaggioProgressBar);
                badgePB.setMax(10);//todo: inserire costante valore max di timbri
                badgePB.setProgress(timbriNumber);
                //Percentage string
                badgeText = findViewById(R.id.percentageTextView);
                badgeText.setText(Integer.toString(timbriNumber*10) + "%");
            }
        }
    }

    //PRIVATE FUNCTIONS-----------------------------------------------------------
    private String getTimbriStr(int timbriNumber)
    {
        //todo inserire costante valore max di timbri
        return "Numero di timbri: " + Integer.toString(timbriNumber) + "/10";
    }

    private String getOmaggiStr(int omaggiNumber)
    {
        return "Piadine omaggio guadagnate: " + Integer.toString(omaggiNumber);
    }

    private void beginScanQRCode()
    {
        new IntentIntegrator(this).initiateScan();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
