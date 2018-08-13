package com.example.ale.piadinapp.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.MainActivity;
import com.example.ale.piadinapp.MyOrderActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.piadinapp.classi.ServiceNotification;
import com.example.ale.utility.CustomRequest;
import com.example.ale.utility.VolleyCallback;
import com.example.ale.utility.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CartActivity extends AppCompatActivity implements LocationListener{


    CartItemAdapter adapter;
    ArrayList<CartItem> Items = new ArrayList<CartItem>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<Ingrediente>();
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    VolleyCallback durataCallBack;
    private static Context context;


    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvTot= (TextView)findViewById(R.id.tv_total);
        Button clearCart = (Button)findViewById((R.id.clear_cart));

        context=getApplicationContext();


        data = cs.ViewAll(getApplicationContext());
        createNotificationChannel();

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svuotaCarrello();
                recreate();
            }
        });

        cs.persistData(getApplicationContext(),true);

        // ricevo l'elemento inserito nel carrello

        String id;
        if (data == null || data.size()==0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_LONG);
            toast.show();
        } else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {

                int numero = k + 1;
                id = "Piadina " + numero;
                String formato = cs.getString(id, "formato", getApplicationContext());
                String impasto = cs.getString(id, "impasto", getApplicationContext());
                String ingredients = cs.getString(id, "ingredienti", getApplicationContext());
                Double prezzo= Double.valueOf(cs.getDouble(id, "prezzo", getApplicationContext()));
                String nome = cs.getString(id, "nome", getApplicationContext());
                String identifier =cs.getString(id,"identifier",getApplicationContext());

                //ricostruisco gli ingredienti e l'stanza della classe CartItem

                String strippedIngredients = ingredients.replaceAll("\\[", "").replaceAll("\\]", "");
                List<String> ings = Arrays.asList(strippedIngredients.split(","));

                ingredienti = new ArrayList<>();
                ingredienti.clear();

                for (String ing : ings) {

                    ingredienti.add(new Ingrediente(ing));
                }

                CartItem item = new CartItem(nome, formato, impasto, prezzo, ingredienti,identifier);
                Items.add(item);

                k++;

            }

        }


        final RecyclerView rv = findViewById(R.id.cart_item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(getApplicationContext(), Items, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

                String identify= adapter.getItem(position).getIdentifier();
                cs.RemoveId(identify,getApplicationContext());
                adapter.removeItem(position);
                setTotale();


                Toast.makeText(CartActivity.this, "Item rimosso dal carrello", Toast.LENGTH_SHORT).show();

                if(adapter.getItemCount() == 0){
                    Toast.makeText(CartActivity.this, "Il carrello è vuoto!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCartItemClicked(int position) {


                Intent intent = new Intent(getApplicationContext(),CustomizePiadinaActivity.class);
                Gson gson = new Gson();
                String modificaPiadinaAsAString = gson.toJson(adapter.getItem(position));
                intent.putExtra("modificaPiadina",modificaPiadinaAsAString);
                startActivity(intent);
                finish();

            }
        });

        rv.setAdapter(adapter);

        setTotale();

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CartActivity.this, R.drawable.piadina_divider));
        rv.addItemDecoration(itemDecorator);

        Button notify = (Button) findViewById(R.id.effettua_ordine);
        notify.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

            }

        });

        Button startService = (Button) findViewById(R.id.start_service);
        startService.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
                if (EasyPermissions.hasPermissions(getApplicationContext(), perms)) { }
                else
                {
                    EasyPermissions.requestPermissions(getParent(), "Richiesta permesso accesso posizione",1, perms);
                }

                Log.d("PERMESSI",""+checkLocationPermission());

                startService(v);

            }

        });

        final Button stopService = (Button) findViewById(R.id.stop_service);
        stopService.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                stopNotificationService();

            }

        });


    }

    public void stopNotificationService(){

        Intent intent = new Intent(CartActivity.getAppContext(), ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        stopService(intent);
        pintent.cancel();
        alarm.cancel(pintent);
        stopService(new Intent(getApplicationContext(),ServiceNotification.class));
    }

    public void setTotale(){
        double tot=0;
        for (int i =0; i<adapter.getItemCount();i++)
        {
            tot= tot +adapter.getItem(i).getPrezzo();
        }

        String totale = "Totale: " + String.valueOf(tot);
        TextView tvTot= (TextView)findViewById(R.id.tv_total);
        tvTot.setText(totale);
    }

    public void svuotaCarrello (){

        if (data==null || data.size()==0)
        {

            Toast toast = Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_LONG);
            toast.show();
        }

        else
        {

            int numeroItem=adapter.getItemCount();
            for (int i =0; i<numeroItem;i++)
            {
                int numPiadina = i+1;
                cs.RemoveId("Piadina "+numPiadina,getApplicationContext());
                adapter.removeItem(0);

            }
            Items.clear();
            data=null;
        }

    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "piadina_channel";
            String description = "notifiche_paidina";
            NotificationChannel channel = new NotificationChannel("piadina_channel", name, NotificationCompat.PRIORITY_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")


    public void startService(View v)
    {
        Log.d("START","SERVICE: Start Service");
        startService(new Intent(this,ServiceNotification.class));
        Intent notificationIntent = new Intent(this, ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, notificationIntent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
        //SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10000, pintent);
    }

    public static Context getAppContext(){
        return CartActivity.context;
    }


    @Override
    public void onLocationChanged(Location location) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}


