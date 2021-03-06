package com.example.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;

import com.example.android.R;
import com.example.android.utility.SessionManager;

import java.util.HashMap;

public class MyProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int UPDATE_USER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //session = new SessionManager(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateUserDataStrings();
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
        getMenuInflater().inflate(R.menu.my_profile, menu);
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
/*        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {


        } else if (id == R.id.tessera) {

            Intent intent = new Intent(this, BadgeActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.logout) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            final ProgressDialog progressDialog = new ProgressDialog(MyProfileActivity.this,
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
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        //Check if is the right request
        if(requestCode == UPDATE_USER_REQUEST) {
            if(resultCode == RESULT_OK) {
                //Log.d("PROFILE","Update user data strings");
                //todo update user strings
                updateUserDataStrings();
            }
        }
    }

    public void editUserInfos(View view) {
        Intent intent = new Intent(this, EditUserActivity.class);
        //startActivity(intent);
        startActivityForResult(intent,UPDATE_USER_REQUEST);
    }

    //PRIVATE FUNCTIONS--------------------------------------------------------
    private void updateUserDataStrings()
    {
        // ottengo le informazioni dall'utente dalle preferenze condivise e le imposto nella barra.
        HashMap<String, String> utente;
        utente = SessionManager.getUserDetails(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView txtProfileName = navigationView.getHeaderView(0).findViewById(R.id.username_nav);
        TextView txtProfileEmail = navigationView.getHeaderView(0).findViewById(R.id.email_nav);
        TextView emailText = findViewById(R.id.profile_email);
        TextView nameText = findViewById(R.id.profile_username);
        TextView phoneText = findViewById(R.id.profile_phone);

        if(utente == null) {
            txtProfileName.setText("");
            txtProfileEmail.setText("");
            emailText.setText("");
            nameText.setText("");
            phoneText.setText("");
        } else {
            String nome = utente.get("name");
            String mail = utente.get("email");
            String telefono = utente.get("phone");

            txtProfileName.setText(nome);
            txtProfileEmail.setText(mail);
            emailText.setText(mail);
            nameText.setText(nome);
            phoneText.setText(telefono);
        }
    }
    //-------------------------------------------------------------------------
}
