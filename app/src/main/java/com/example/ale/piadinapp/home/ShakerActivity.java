package com.example.ale.piadinapp.home;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.ale.piadinapp.BadgeActivity;
import com.example.ale.piadinapp.HomeActivity;
import com.example.ale.piadinapp.MyOrderActivity;
import com.example.ale.piadinapp.MyProfileActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.WeAreHereActivity;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class ShakerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    private ShakeDetector shakeDetector;
    DBHelper helper;
    ArrayList<Piadina> randomPiadinaList;
    private Random randomGenerator;
    private Piadina randPiadina;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new DBHelper(this);
        //session = new SessionManager(this);

        randomPiadinaList = helper.getPiadine();
        final ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        final TextView tv_shake = (TextView)findViewById(R.id.tv_shake);
        final CardView cv_piadina = (CardView)findViewById(R.id.cv_piadina);
        final Button btn_personalizza = (Button)findViewById(R.id.btn_personalizza);
        final TextView tv_4 = (TextView)findViewById(R.id.textView4);
        final TextView titolo = (TextView)findViewById(R.id.textViewTitle);
        final TextView price = (TextView)findViewById(R.id.textViewPrezzo);
        final Button shake_button = (Button) findViewById(R.id.button_shake);
        final TextView tv_ingredienti = (TextView)findViewById(R.id.textViewIngredients);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        shake_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shake_button.setText("SCUOTI!");
                    shakeDetector = new ShakeDetector(options).start(getApplicationContext(), new ShakeCallback() {
                    @Override
                    public void onShake() {


                        randPiadina = getRandomPiadina();
                        String nome = randPiadina.getNome();
                        double prezzo = randPiadina.getPrice();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(300,VibrationEffect.DEFAULT_AMPLITUDE));
                        }else{
                            //deprecated in API 26
                            vibrator.vibrate(300);
                        }
                        titolo.setText(nome);
                        price.setText(String.valueOf(prezzo));
                        tv_ingredienti.setText(randPiadina.printIngredienti());
                        tv_shake.setVisibility(View.GONE);
                        tv_4.setVisibility(View.VISIBLE);
                        cv_piadina.setVisibility(View.VISIBLE);
                        btn_personalizza.setVisibility(View.VISIBLE);
                        shakeDetector.stopShakeDetector(getBaseContext());
                        shake_button.setText("CLICCAMI");

                    }
                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ottengo le informazioni dall'utente dalle preferenze condivise e le imposto nella barra.
        HashMap<String, String> utente;
        //utente = session.getUserDetails();
        utente = SessionManager.getUserDetails(this);

        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username_nav);
        TextView txtProfileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_nav);

        if(utente == null) {
            txtProfileName.setText("");
            txtProfileEmail.setText("");
        } else {
            txtProfileName.setText(utente.get("name"));
            txtProfileEmail.setText(utente.get("email"));
        }

        btn_personalizza.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CustomizePiadinaActivity.class);
                Gson gson = new Gson();
                String randomPiadinaAsAString = gson.toJson(randPiadina);
                intent.putExtra("randomPiadina",randomPiadinaAsAString);
                startActivity(intent);
                finish();
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
        getMenuInflater().inflate(R.menu.shaker, menu);
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

    public Piadina getRandomPiadina()
    {
        randomGenerator = new Random();
        int index = randomGenerator.nextInt(randomPiadinaList.size());
        Piadina randomPiadina = randomPiadinaList.get(index);
        return randomPiadina;
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

                            final ProgressDialog progressDialog = new ProgressDialog(ShakerActivity.this,
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
            finish();


        } else if (id == R.id.where) {

            Intent intent = new Intent(this, WeAreHereActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.ordini) {

            Intent intent = new Intent(this, MyOrderActivity.class);
            startActivity(intent);
            finish();

        } else if(id == R.id.shaker){

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
