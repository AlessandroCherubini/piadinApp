package com.example.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.example.android.classi.Piadina;
import com.example.android.services.NotificationService;
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
    Toolbar toolbar;
    static Context mContext;
    Calendar dateCalendar;

    String timestampOrdine;
    double totaleOrdine;
    boolean fromOrdine = false;

    Bundle bundle;
    FasceOrarioFragment fragmentFasce;

    private static final int JOB_ID = 1;
    private static final int ONE_MIN = 60 * 1000;


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

        toolbar = findViewById(R.id.toolbar);
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

        riempiCarrello();

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
                                bundle.putInt("quantitaCarrello", contaPiadine());
                                bundle.putDouble("totaleOrdine", totaleOrdine);
                                bundle.putParcelableArrayList("cartItems", cartItems);

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

                        }
                    }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            setSupportActionBar(toolbar);
            getSupportActionBar().show();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    public void setTotale(){
        // Somma dei prezzi delle piadine
        double tot = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            tot = tot + adapter.getItem(i).getPrezzo();
        }

        totaleOrdine = tot;
        BigDecimal totale = new BigDecimal(tot);
        totale= totale.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        tvTot.setText("Totale: " + totale.toPlainString().replace(".", ",") + " €");
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

            ComponentName component = new ComponentName(mContext, NotificationService.class);

            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("orarioRitiro", orarioRitiro);
            bundle.putString("dataRitiro", timestampOrdine);

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                    // schedule it to run any time between 1 - 5 minutes
                    //.setMinimumLatency(ONE_MIN)
                    //.setOverrideDeadline(5 * ONE_MIN)
                    .setPeriodic(JobInfo.getMinPeriodMillis(), JobInfo.getMinFlexMillis())
                    //.setPersisted(true)
                    .setExtras(bundle);

            JobScheduler jobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());

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

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && fromOrdine == true) {
            startService();
            finish();
        }
    }
    @Override
    public void onResume(){
        super.onResume();

        riempiCarrello();
        adapter.notifyDataSetChanged();
        setTotale();
    }

    @AfterPermissionGranted(1)
    public void locationAndNotification() {
        if (checkLocationPermission()) {
            // Have permissions, do the thing!
            LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                fromOrdine = true;
            }
            else{
                startService();
                finish();
            }
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

    public void riempiCarrello(){
        cartItems.clear();
        // ricevo l'elemento inserito nel carrello
        if (data == null || data.size()==0) {
        } else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                String id;

                int numero = k + 1;
                id = "Piadina " + numero;
                String nome = cs.getString(id, "nome", mContext);
                String formato = cs.getString(id, "formato", mContext);
                String impasto = cs.getString(id, "impasto", mContext);
                String ingredients = cs.getString(id, "ingredienti", mContext);
                Double prezzo = cs.getDouble(id, "prezzo", mContext);
                Integer quantita = cs.getLong(id, "quantita", mContext).intValue();
                Integer rating = cs.getLong(id, "rating", mContext).intValue();
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
    }

    public int contaPiadine(){
        int numeroPiadine = 0;
        for(CartItem elemento:cartItems){
            numeroPiadine = numeroPiadine + elemento.getQuantita();
        }

        return numeroPiadine;
    }

}


