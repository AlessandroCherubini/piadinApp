package com.example.ale.piadinapp.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.HomeActivity;
import com.example.ale.piadinapp.LoginActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Ordine;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.piadinapp.classi.ServiceNotification;
import com.example.ale.utility.DBHelper;
import com.example.ale.utility.VolleyCallback;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import com.example.ale.utility.*;

public class CartActivity extends AppCompatActivity implements LocationListener{

    CartItemAdapter adapter;
    ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<Ingrediente>();
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    DBHelper helper;
    TextView tvTot;
    View view;
    static Context mContext;
    Calendar dateCalendar;
    String timestampOrdine;
    double totaleOrdine;
    String notaOrdine;
    long lastUpdateOrdine;
    ArrayList<Piadina> piadineOrdine;
    Ordine ordine;
    SessionManager session;
    HashMap<String, String> utente;
    VolleyCallback durataCallBack;

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mContext = this;
        helper = new DBHelper(this);
        //utente = session.getUserDetails();
        utente = SessionManager.getUserDetails(this);

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

        cs.persistData(getApplicationContext(),true);
        // ricevo l'elemento inserito nel carrello

        String id;
        if (data == null || data.size()==0) {
            Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_SHORT).show();
        } else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                Map map = entry.getValue();

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

        Button notify = (Button) findViewById(R.id.effettua_ordine);
        notify.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                if(cartItems.size() == 0){
                    Toast.makeText(CartActivity.this, "Carrello vuoto!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Calendar currentDate = Calendar.getInstance();
                dateCalendar = Calendar.getInstance();

               new CustomDatePickerDialog(mContext, R.style.OrologioOrdini,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                dateCalendar.set(year, monthOfYear, dayOfMonth);
                                new TimePickerDialog(mContext, R.style.OrologioOrdini, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        dateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        dateCalendar.set(Calendar.MINUTE, minute);
                                        Log.v("ORARIO", "The choosen one "+ dateCalendar.getTime());

                                        //GregorianCalendar gregOrario = new GregorianCalendar(year, monthOfYear, dayOfMonth, hourOfDay, minute, 00);
                                        //timestampOrdine = gregOrario.getTimeInMillis();
                                        lastUpdateOrdine = dateCalendar.getTimeInMillis();
                                        timestampOrdine = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateCalendar.getTime());
                                        Log.d("ORARIO", "" + lastUpdateOrdine + "" + timestampOrdine);

                                        addNotaOrdine();

                                    }
                                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                            }
                        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();

            }

        });

        final Button startService = (Button) findViewById(R.id.start_service);
        startService.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
                if (EasyPermissions.hasPermissions(CartActivity.this, perms)) {
                    Log.d("PERMESSI","" + checkLocationPermission());

                    startService(v);
                }
                else {
                    view = v;
                    EasyPermissions.requestPermissions(CartActivity.this, "Richiesta permesso per l\'utilizzo della posizione",1, perms);
                    startService(v);
                }
            }

        });

        final Button stopService = (Button) findViewById(R.id.stop_service);
        stopService.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                stopNotificationService();

            }

        });

    }

    public ArrayList<Piadina> creaPiadineOrdine(ArrayList<CartItem> cartItems){
        ArrayList<Piadina> piadineOrdine = new ArrayList<>();

        for(CartItem item: cartItems){
            String nomePiadina = item.getNome();
            String formatoPiadina = item.getFormato();
            String impastoPiadina = item.getImpasto();
            ArrayList<Ingrediente> ingredientiPiadina = item.getIngredienti();
            double prezzoPiadina = item.getPrezzo();
            int quantitaPiadina = item.getQuantita();
            int ratingPiadina = item.getRating();

            Piadina piadina = new Piadina(nomePiadina, formatoPiadina, impastoPiadina, ingredientiPiadina,
                    prezzoPiadina, quantitaPiadina, ratingPiadina);

            piadineOrdine.add(piadina);
        }

        return piadineOrdine;
    }

    public void addNotaOrdine(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_nota, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.testo_nota);

        dialogBuilder.setTitle("Nota dell'ordine:");
        dialogBuilder.setIcon(R.drawable.ic_note_add_black_24dp);
        dialogBuilder.setMessage("È data la possibilità di scrivere una eventuale nota per il gestore.");
        dialogBuilder.setPositiveButton("Ok, ordina", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                notaOrdine = editText.getText().toString().trim();

                // Procediamo con l'ordine!!
                final ProgressDialog progressDialog = new ProgressDialog(CartActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Evasione dell'ordine...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                finishOrder();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });

        dialogBuilder.setNegativeButton("Annulla ordine", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void finishOrder(){
        String emailUtente = utente.get("email");
        String telefonoUtente = utente.get("phone");
        piadineOrdine = creaPiadineOrdine(cartItems);
        ordine = new Ordine(0, emailUtente, telefonoUtente, timestampOrdine, totaleOrdine, piadineOrdine,
                notaOrdine, lastUpdateOrdine);

        helper.insertOrdine(ordine);
        helper.printTabellaOrdine();

        OnlineHelper onlineHelper = new OnlineHelper();
        onlineHelper.addOrderinExternalDB(mContext, ordine);

        svuotaCarrello();
        finish();
    }

    public void stopNotificationService(){
        Intent intent = new Intent(CartActivity.this, ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        stopService(intent);
        pintent.cancel();
        alarm.cancel(pintent);
        stopService(new Intent(getApplicationContext(),ServiceNotification.class));
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
        tvTot.setText("Totale: " + totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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


    public void startService(View v) {
        Log.d("START","SERVICE: Start Service");
        startService(new Intent(this,ServiceNotification.class));
        Intent notificationIntent = new Intent(this, ServiceNotification.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, notificationIntent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // Non viene eseguito esattamente ogni x millis perchè decide android quando attivarlo, si potrebbe considerare
        //SetExact ma porta ad un consumo più elevato e non ci interessa una precisione al minuto
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),10000, pintent);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

/*        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Accessi consentiti!
                    Log.d("ACCESSI", "" + grantResults[0]);
                    startService(view);
                } else {
                    // permission denied!
                    Toast.makeText(this, "Permessi negati per la posizione!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }*/

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

}


