package com.example.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.carteasy.v1.lib.Carteasy;
import com.example.android.home.PagerAdapter;
import com.example.android.fragments.TabCreaPiadina;
import com.example.android.fragments.TabLeMiePiadine;
import com.example.android.fragments.TabMenu;
import com.example.android.R;
import com.example.android.utility.SessionManager;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabMenu.OnFragmentInteractionListener,
        TabCreaPiadina.OnFragmentInteractionListener, TabLeMiePiadine.OnFragmentInteractionListener {

    TextView textCartItemCount;
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    int mCartItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = cs.ViewAll(getApplicationContext());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Menù"));
        tabLayout.addTab(tabLayout.newTab().setText("Crea Piadina"));
        tabLayout.addTab(tabLayout.newTab().setText("Le mie Piadine"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }


    @Override
    public void onResume() {
        super.onResume();

        invalidateOptionsMenu();

        data = cs.ViewAll(getApplicationContext());
        mCartItemCount=0;
        if (data==null || data.size()==0) {
            mCartItemCount = 0;

        }
        else{

            for (Map.Entry<Integer, Map> entry : data.entrySet()) {

                mCartItemCount++;
            }
        }

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        return true;
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

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {

            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
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

        } else if (id == R.id.ordini) {

                Intent intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);

            }else if(id == R.id.shaker){

                Intent intent = new Intent(this, ShakerActivity.class);
                startActivity(intent);

            }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

     public void onFragmentInteraction(Uri uri){}

}
