package com.example.ale.piadinapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.piadinapp.home.PagerAdapter;
import com.example.ale.piadinapp.home.TabCreaPiadina;
import com.example.ale.piadinapp.home.TabLeTuePiadine;
import com.example.ale.piadinapp.home.TabMenu;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.SessionManager;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabMenu.OnFragmentInteractionListener,
        TabCreaPiadina.OnFragmentInteractionListener, TabLeTuePiadine.OnFragmentInteractionListener {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#552d27")));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(this);
        /*DBHelper helper = new DBHelper(this);
        helper.printIngredientiTable();*/

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Menù"));
        tabLayout.addTab(tabLayout.newTab().setText("Crea Piadina"));
        tabLayout.addTab(tabLayout.newTab().setText("Le tue Piadine"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
        utente = session.getUserDetails();

        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username_nav);
        txtProfileName.setText(utente.get("name"));

        TextView txtProfileEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_nav);
        txtProfileEmail.setText(utente.get("email"));
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
        getMenuInflater().inflate(R.menu.home, menu);
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

        } else if (id == R.id.tessera) {

                Intent intent = new Intent(this, BadgeActivity.class);
                startActivity(intent);

        } else if (id == R.id.logout) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this,
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

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.ordini) {

                Intent intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);

            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

     public void onFragmentInteraction(Uri uri){}

}
