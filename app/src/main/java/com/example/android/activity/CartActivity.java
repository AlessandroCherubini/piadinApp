package com.example.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.adapters.CartItemAdapter;
import com.example.android.classi.CartItem;
import com.example.android.classi.FasciaOraria;
import com.example.android.classi.Ingrediente;
import com.example.android.classi.ServiceNotification;
import com.example.android.fragments.FasceOrarioFragment;
import com.example.android.home.ClickListener;
import com.example.android.utility.DBHelper;
import com.example.android.utility.VolleyCallback;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import com.example.android.utility.*;

public class CartActivity extends AppCompatActivity implements LocationListener{


    CartItemAdapter adapter;
    ArrayList<CartItem> cartItems = new ArrayList<>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<>();
    ArrayList<FasciaOraria> fasceOrarie = new ArrayList<>();
    String orarioRitiro;

    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    DBHelper helper;
    TextView tvTot;
    View view;
    static Context mContext;
    Calendar dateCalendar;

    String timestampOrdine;
    double totaleOrdine;

    Bundle bundle;
    FasceOrarioFragment fragmentFasce;


    VolleyCallback durataCallBack;
    View v;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_cart);
        orarioRitiro = "";

        bundle = new Bundle();

        mContext = this;
        helper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTot = findViewById(R.id.tv_total);
        Button clearCart = findViewById((R.id.clear_cart));

        data = cs.ViewAll(getApplicationContext());
        createNotificationChannel();

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svuotaCarrello();
                recreate();
            }
        });

        //cs.persistData(getApplicationContext(),true);
        // ricevo l'elemento inserito nel carrello


        if (data == null || data.size()==0) {
            Toast.makeText(mContext, "Non ci sono elementi nel carrello", Toast.LENGTH_SHORT).show();
        } else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                Map map = entry.getValue();
                String id;

                int numero = k + 1;
                id = "Piadina " + numero;
                String nome = cs.getString(id, "nome", mContext);
                String formato = cs.getString(id, "formato", mContext);
                String impasto = cs.getString(id, "impasto", mContext);
                String ingredients = cs.getString(id, "ingredienti", mContext);
                Double prezzo = cs.getDouble(id, "prezzo", mContext);
                Integer quantita = Integer.parseInt((String) map.get("quantita"));
                Integer rating = Integer.parseInt((String) map.get("rating"));
                String identifier = cs.getString(id,"identifier",mContext);

                //ricostruisco gli ingredienti e l'stanza della classe CartItem

                String strippedIngredients = ingredients.replaceAll("\\[", "").replaceAll("\\]", "");
                List<String> ings = Arrays.asList(strippedIngredients.split(", "));

                ingredienti = new ArrayList<>();
                ingredienti.clear();

                for (String ing : ings) {
                    Ingrediente ingrediente = helper.getIngredienteByName(ing);
                    ingredienti.add(ingrediente);
                }

                CartItem item = new CartItem(nome, formato, impasto, prezzo, quantita, rating, ingredienti, identifier);
                cartItems.add(item);

                k++;

            }

        }

        final RecyclerView rv = findViewById(R.id.cart_item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(getApplicationContext(), cartItems, new ClickListener() {
            @Override
            public void onPositionClicked(View view, final int position) {
                switch(view.getId()){
                    case R.id.editButton:
                        Intent intent = new Intent(getApplicationContext(),CustomizePiadinaActivity.class);
                        Gson gson = new Gson();
                        String modificaPiadinaAsAString = gson.toJson(adapter.getItem(position));
                        intent.putExtra("modificaPiadina", modificaPiadinaAsAString);
                        startActivity(intent);
                        break;
                    case R.id.remButton:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        String identify = adapter.getItem(position).getIdentifier();
                                        cs.RemoveId(identify,getApplicationContext());
                                        adapter.removeItem(position);
                                        setTotale();

                                        Toast.makeText(CartActivity.this, "Eliminato!", Toast.LENGTH_SHORT).show();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        // Non si fa niente!
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Elimina");
                        builder.setMessage("Vuoi davvero rimuovere questo elemento dal carrello?").setPositiveButton("Sì, Elimina", dialogClickListener)
                                .setNegativeButton("Annulla", dialogClickListener).show();

                        break;
                }

            }
        });

        rv.setAdapter(adapter);
        setTotale();

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CartActivity.this, R.drawable.piadina_divider));
        rv.addItemDecoration(itemDecorator);

        Button notify = findViewById(R.id.effettua_ordine);
        notify.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                if(cartItems.size() == 0){
                    Toast.makeText(mContext, "Carrello vuoto!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Calendar currentDate = Calendar.getInstance();
                dateCalendar = Calendar.getInstance();

               new CustomDatePickerDialog(mContext, R.style.OrologioOrdini,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                            dateCalendar.set(year, monthOfYear, dayOfMonth);

                            /*if(dateCalendar.getTimeInMillis() < currentDate.getTimeInMillis()){
                                Toast.makeText(mContext, "Data selezionata antecedente la data corrente! Riprova!", Toast.LENGTH_SHORT ).show();
                            }else{*/
                                timestampOrdine = new SimpleDateFormat("yyyy-MM-dd").format(dateCalendar.getTime());
                                bundle.putString("dataRichiesta", timestampOrdine);
                                bundle.putInt("quantitaCarrello", cartItems.size());
                                bundle.putDouble("totaleOrdine", totaleOrdine);
                                bundle.putParcelableArrayList("cartItems", cartItems);

                                //bundle.putParcelableArrayList("fasceOrarie", fasceOrarie);
                                // set Fragmentclass Arguments


                                // Create new fragment and transaction
                                fragmentFasce = new FasceOrarioFragment();
                                fragmentFasce.setArguments(bundle);
                                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                // Replace whatever is in the fragment_container view with this fragment,
                                // and add the transaction to the back stack
                                transaction.replace(R.id.container_fasce, fragmentFasce);
                                transaction.addToBackStack(null);
                                // Commit the transaction
                                transaction.commit();
                                // preparo per passarli nel fragment

                                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_fasce, fragmentFasce).commit();
                            //}

                            //addNotaOrdine();
                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }

        });



        /* START SERVICE */
        final Button startService = (Button) findViewById(R.id.start_service);
        startService.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

               /* String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
                if (EasyPermissions.hasPermissions(CartActivity.this, perms)) {
                    Log.d("PERMESSI","" + checkLocationPermission());

                   if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                   {
                       Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                       startActivity(intent);
                   }
                    else{startService(v);};

                }
                else {

                    EasyPermissions.requestPermissions(CartActivity.this, "Richiesta permesso per l\'utilizzo della posizione",1, perms);
                    String [] permission = {"android.permission.ACCESS_FINE_LOCATION","android.permission.INTERNET"};
                    int res [] = new int[2];
                    res[0]= checkCallingOrSelfPermission(permission[0]);
                    res[1]= checkCallingOrSelfPermission(permission[1]);
                    onRequestPermissionsResult(1,perms,res);
                }*/
               locationAndNotification();
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
        Intent intent = new Intent(CartActivity.this, ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        stopService(intent);
        pintent.cancel();
        alarm.cancel(pintent);
        stopService(new Intent(mContext,ServiceNotification.class));
        Log.d("SERVICE", "Servizio stoppato!");
    }

    public void setTotale(){
        // Somma dei prezzi delle piadine
        double tot = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            tot = tot + adapter.getItem(i).getPrezzo();
        }

        totaleOrdine = tot;
        BigDecimal totale = new BigDecimal(tot);
        totale= totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        tvTot.setText("Totale: " + totale.toPlainString() + " €");
    }

    public void svuotaCarrello (){
        if (data == null || data.size() == 0) {
            Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_SHORT).show();
        }else {
            int numeroItem = adapter.getItemCount();
            for (int i = 0; i < numeroItem; i++) {
                int numPiadina = i+1;
                cs.RemoveId("Piadina " + numPiadina, getApplicationContext());
                adapter.removeItem(0);
            }
            cartItems.clear();
            data = null;
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


    public void startService() {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("START", "SERVICE: Start Service");
            Intent intentService = new Intent(this, ServiceNotification.class);
            intentService.putExtra("orarioRitiro", orarioRitiro);
            startService(intentService);

            Intent notificationIntent = new Intent(this, ServiceNotification.class);
            PendingIntent pintent = PendingIntent.getService(this, 0, notificationIntent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
            //SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pintent);
        }
    }


    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // Accessi consentiti!
                    if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }

                } else {
                    // permission denied!
                    Toast.makeText(this, "Permessi negati per la posizione!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
*/
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

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onRestart(){
        super.onRestart();

        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startService();

        }
    }

    @AfterPermissionGranted(1)
    public void locationAndNotification() {
        if (checkLocationPermission()) {
            // Have permissions, do the thing!
            LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            else{startService();}
        } else {
            // Ask for both permissions
            String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
            EasyPermissions.requestPermissions(CartActivity.this, "Richiesta permesso per l\'utilizzo della posizione",1, perms);
        }
    }

    public String getOrarioRitiro() {
        return orarioRitiro;
    }

    public void setOrarioRitiro(String orarioRitiro) {
        this.orarioRitiro = orarioRitiro;
    }

}


