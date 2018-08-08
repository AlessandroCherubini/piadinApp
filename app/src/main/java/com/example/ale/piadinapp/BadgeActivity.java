package com.example.ale.piadinapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.ale.piadinapp.classi.Timbro;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

public class BadgeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ottengo le informazioni dall'utente dalle preferenze condivise e le imposto nella barra.
        final HashMap<String, String> utente;
        utente = session.getUserDetails();

        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username_nav);
        txtProfileName.setText(utente.get("name"));

        TextView txtProfileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_nav);
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

        //Get badge infos
        final HashMap<String,Integer> badgeData;
        badgeData = session.getBadgeDetails();

        //Inserimento numero di timbri
        TextView timbriTV = findViewById(R.id.timbriTextView);
        timbriTV.setText(getTimbriStr(badgeData.get("timbri")));
        //Inserimento numero di omaggi
        TextView omaggiTV = findViewById(R.id.omaggiTextView);
        omaggiTV.setText(getOmaggiStr(badgeData.get("omaggi")));

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
                                            session.logoutUser();
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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.where) {

            Intent intent = new Intent(this, WeAreHereActivity.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.ordini) {

            Intent intent = new Intent(this, MyOrderActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                if(session == null) {
                    session = new SessionManager(this);
                }
                final HashMap<String,Integer> badgeData;
                badgeData = session.getBadgeDetails();

                int timbriNumber = badgeData.get("timbri");
                int omaggiNumber = badgeData.get("omaggi");
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
                //session.updateTimbriAndOmaggiValue(timbriNumber,omaggiNumber);
                //TEST
                SharedPreferences pref = getSharedPreferences("piadinApp", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("timbri",timbriNumber);
                edit.commit();
                //TEST

                //Update labels strings
                TextView badgeText = findViewById(R.id.timbriTextView);
                badgeText.setText(getTimbriStr(timbriNumber));
                badgeText = findViewById(R.id.omaggiTextView);
                badgeText.setText(getOmaggiStr(omaggiNumber));
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
