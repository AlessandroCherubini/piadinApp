package com.example.ale.piadinapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.ale.utility.SessionManager;
import com.google.zxing.BarcodeFormat;
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
        HashMap<String, String> utente;
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

        //Riempimento ProgressBar dei timbri
        RoundCornerProgressBar timbriProgress = findViewById(R.id.timbriProgressBar);
        timbriProgress.setMax(10); //todo inserire una costante per il max numero di piadine prima di quella omaggio
        timbriProgress.setProgress(5); //todo recupero numero timbri corrente da DB
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
}
