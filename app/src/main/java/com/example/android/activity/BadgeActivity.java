package com.example.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.services.BadgeUpdateService;
import com.example.android.utility.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

public class BadgeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final char SEPARATOR_QR_STR = ';';
    private static final int SERVICE_INTERVAL = 10000;

    //Broadcast receiver per update stringhe del badge------
    private BroadcastReceiver updateStringsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBadgeValuesString();
            //Dopo aver aggiornato le stringhe, annullo la ripetizione del service
            stopUpdateService();
        }
    };
    //------------------------------------------------------

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

        startUpdateService();
        //Registro il receiver
        registerReceiver(updateStringsReceiver,new IntentFilter(BadgeUpdateService.BROADCAST_ACTION));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        startUpdateService();
        registerReceiver(updateStringsReceiver,new IntentFilter(BadgeUpdateService.BROADCAST_ACTION));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //Stop update badge service
        stopUpdateService();
        unregisterReceiver(updateStringsReceiver);
    }

    public void onPause()
    {
        super.onPause();
        stopUpdateService();
        unregisterReceiver(updateStringsReceiver);
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

    private void startUpdateService()
    {
        Log.d("START","SERVICE: Update badge service");
        startService(new Intent(this, BadgeUpdateService.class));

        Intent intent = new Intent(this,BadgeUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        /*
         * Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
         * SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
         */
        if(pendingIntent != null)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SERVICE_INTERVAL, pendingIntent);
    }

    private void stopUpdateService()
    {
        Intent intent = new Intent(this,BadgeUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);

        stopService(intent);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent.cancel();
        alarm.cancel(pendingIntent);
        stopService(new Intent(getApplicationContext(),BadgeUpdateService.class));
        Log.d("SERVICE","Update Badge stopped");
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}