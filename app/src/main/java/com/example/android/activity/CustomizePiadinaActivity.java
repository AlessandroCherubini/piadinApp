package com.example.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.classi.CartItem;
import com.example.android.classi.Ingrediente;
import com.example.android.classi.Piadina;
import com.example.android.fragments.TabMenu;
import com.example.android.home.CategorieIngredientiAdapter;
import com.example.android.home.IngredientsAdapter;
import com.example.android.utility.DBHelper;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class CustomizePiadinaActivity extends AppCompatActivity
        implements TabMenu.OnFragmentInteractionListener, IngredientsAdapter.ItemClickListener {

    Piadina chosenPiadina;
    IngredientsAdapter adapter;
    CategorieIngredientiAdapter categorieAdapter;
    DBHelper helper;
    String nomePiadina;
    String formatoPiadina;
    String impastoPiadina;
    static int quantitaPiadina;
    static int ratingPiadina;
    ArrayList<Ingrediente> ingredientiPiadina;
    RecyclerView recyclerView;
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    CartItem cartItem;

    public final static Double IMPASTO_INTEGRALE = 0.30;
    public final static Double FORMATO_BABY = -1.0;
    public final static Double FORMATO_ROTOLO = 2.0;

    public static double totalePiadina = 0;
    static double totaleImpastoEFormato = 0;
    public static double totaleIngredienti = 0;
    TextView prezzoPiadina;
    double prezzoPiadinaSingola;
    public Context mContext;
    String identificatore;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_piadina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();

        // uso l'extra per prendere la piadina selezionata
        helper = new DBHelper(this);
        Intent intent = getIntent();
        TextView name = findViewById(R.id.nome_piadina);

        if (intent.getExtras().get("indexPiadina")!= null){

            int position = intent.getIntExtra("indexPiadina",0);
            chosenPiadina = helper.getPiadinaByPosition((long) position + 1);
        }
        else if (intent.getExtras().get("randomPiadina") != null){
            Gson gson = new Gson();
            String chosenPiadinaString = getIntent().getStringExtra("randomPiadina");
            chosenPiadina = gson.fromJson(chosenPiadinaString, Piadina.class);
        }
        else if (intent.getExtras().get("modificaPiadina") != null) {
            isEdit = true;
            Gson gson = new Gson();
            String chosenPiadinaString = getIntent().getStringExtra("modificaPiadina");
            cartItem = gson.fromJson(chosenPiadinaString, CartItem.class);
            chosenPiadina = cartItem.cartItemToPiadina();
            ingredientiPiadina = chosenPiadina.getIngredienti();
            totalePiadina = chosenPiadina.getPrice();
            quantitaPiadina = chosenPiadina.getQuantita();
            ratingPiadina = chosenPiadina.getRating();

            prezzoPiadinaSingola = chosenPiadina.getPrice() / chosenPiadina.getQuantita();
            // Set dei radio button per Formato e Impasto
            setRadioButtons(chosenPiadina);

            Button editButton = findViewById(R.id.addKart);
            editButton.setText("Conferma Modifica");
            editButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_black_24dp, 0, 0, 0);
        }
            //TODO: sistemare problemi

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Attributi variabili della piadina selezionata
        nomePiadina = chosenPiadina.getNome();
        formatoPiadina = chosenPiadina.getFormato();
        impastoPiadina = chosenPiadina.getImpasto();
        quantitaPiadina = chosenPiadina.getQuantita();
        ratingPiadina = chosenPiadina.getRating();
        totaleImpastoEFormato = chosenPiadina.getPrice();
        totalePiadina = totaleImpastoEFormato;

        prezzoPiadina = findViewById(R.id.prezzoTotalePiadina);

        setTotalePiadina(totalePiadina);

        TextView nomePiadina = findViewById(R.id.nome_piadina);
        nomePiadina.setText(chosenPiadina.getNome());

        // Radio Button
        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);
        rb1.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);

        final Button button = findViewById(R.id.addKart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addQuantitaPiadina();
            }
        });

        // RecyclerView per gli ingredienti presenti nella Piadina.

        // Gestione del recupero degli ingredienti
        if(savedInstanceState == null || !savedInstanceState.containsKey("ingredienti")) {
            ingredientiPiadina = chosenPiadina.getIngredienti();
        }
        else{
            ingredientiPiadina = savedInstanceState.getParcelableArrayList("ingredienti");
        }

        recyclerView = findViewById(R.id.ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IngredientsAdapter(this, ingredientiPiadina);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CustomizePiadinaActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CustomizePiadinaActivity.this, R.drawable.piadina_divider));
        recyclerView.addItemDecoration(itemDecorator);

        // Recycle View delle categorie degli ingredienti
        ArrayList<String> categorieIngredienti = helper.getCategorieIngredienti();
        final RecyclerView recyclerViewCategorie = findViewById(R.id.categorie_ingredienti);
        recyclerViewCategorie.setLayoutManager(new LinearLayoutManager(this));
        categorieAdapter = new CategorieIngredientiAdapter(this, categorieIngredienti);

        recyclerViewCategorie.setAdapter(categorieAdapter);

        DividerItemDecoration itemDecoratorCategorie = new DividerItemDecoration(CustomizePiadinaActivity.this, DividerItemDecoration.VERTICAL);
        itemDecoratorCategorie.setDrawable(ContextCompat.getDrawable(CustomizePiadinaActivity.this, R.drawable.piadina_divider));
        recyclerViewCategorie.addItemDecoration(itemDecoratorCategorie);
    }

    @Override
    public void onItemClick(View view, final int position) {
        switch(view.getId()){

            case R.id.allergeniButton:
                DialogInterface.OnClickListener allergeniClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEUTRAL:

                        }
                    }
                };

                AlertDialog.Builder builderAllergenti = new AlertDialog.Builder(this);
                builderAllergenti.setTitle("Allergeni relativi a " + adapter.getItem(position).getName());
                builderAllergenti.setIcon(R.drawable.ic_info_black_24dp);
                builderAllergenti.setMessage(adapter.getItem(position).getListaAllergeni()).
                        setNeutralButton("Ok", allergeniClickListener).show();


                break;
            case R.id.removeButton:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Double prezzoIngrediente = adapter.getItem(position).getPrice();
                                totalePiadina = totalePiadina - prezzoIngrediente;
                                TextView prezzoPiadina = (TextView)findViewById(R.id.prezzoTotalePiadina);

                                BigDecimal totale = new BigDecimal(totalePiadina);
                                totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);
                                prezzoPiadina.setText(totale.toPlainString() + " €");


                                totaleIngredienti = totaleIngredienti - prezzoIngrediente;
                                adapter.removeItem(position);
                                Toast.makeText(CustomizePiadinaActivity.this, "Ingrediente rimosso", Toast.LENGTH_SHORT).show();

                                if(adapter.getItemCount() == 0){
                                    Toast.makeText(CustomizePiadinaActivity.this, "La piadina è vuota!", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                // Non si fa niente!
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Elimina");
                builder.setMessage("Vuoi rimuovere " + adapter.getItem(position).getName() + " ?").setPositiveButton("Sì, Elimina", dialogClickListener)
                        .setNegativeButton("Annulla", dialogClickListener).show();

                break;
        }
    }


    public void onRadioButtonClicked(View v) {
        double prezzoPiadinaBase = chosenPiadina.getPrice();
        TextView prezzoPiadina = findViewById(R.id.prezzoTotalePiadina);

        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_rotolo);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_baby);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_integrale);

        //now check which radio button is selected
        if (rb1.isChecked() && rb4.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb2.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Piadina";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }
        else if (rb2.isChecked() && rb4.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Rotolo";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }
        else if (rb3.isChecked() && rb4.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Baby";
            impastoPiadina = "Normale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb1.isChecked() && rb5.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb3.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Piadina";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb2.isChecked() && rb5.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Rotolo";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

        else if (rb3.isChecked() && rb5.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);

            rb1.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);

            formatoPiadina = "Baby";
            impastoPiadina = "Integrale";
            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY + IMPASTO_INTEGRALE;

            setTotalePiadina(totaleImpastoEFormato + totaleIngredienti);
        }

    }

    private void addQuantitaPiadina(){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_quantita_piadina, null);

        final Button buttonMeno = dialogView.findViewById(R.id.button_quantita_meno);
        final TextView quantitaTesto = dialogView.findViewById(R.id.quantita_testo);
        final Button buttonPiu = dialogView.findViewById(R.id.button_quantita_piu);
        quantitaTesto.setText("Quantità: " + quantitaPiadina);

        final AlertDialog dialog = new AlertDialog.Builder(CustomizePiadinaActivity.this)
                .setView(dialogView)
                .setTitle("Quantità della Piadina:")
                .setIcon(R.drawable.ic_impasto_tradizionale)
                .setMessage("Scegli la quantità da ordinare!")
                .setPositiveButton("Ok, ordina", null) //Set to null. We override the onclick
                .setNegativeButton("Annulla", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isEdit){
                            editItemCarrello();
                            dialog.dismiss();
                            finish();
                        }else{
                            aggiungiAlCarrello();
                            Snackbar.make(view, "Piadina aggiunta al carrello", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                     }
                });

                Button buttonAnnulla = (dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonAnnulla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantitaPiadina = 1;
                        dialog.dismiss();
                    }
                });

                buttonMeno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(quantitaPiadina != 1) {
                            quantitaPiadina = quantitaPiadina - 1;
                            quantitaTesto.setText("Quantità: " + String.valueOf(quantitaPiadina));
                        }
                    }
                });

                buttonPiu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantitaPiadina = quantitaPiadina + 1;
                        quantitaTesto.setText("Quantità: " + String.valueOf(quantitaPiadina));
                    }
                });

            }
        });
        dialog.show();

    }
    private void aggiungiAlCarrello(){
        data = cs.ViewAll(getApplicationContext());
        String id;

        if (data == null || data.size() == 0) {
            id = "Piadina " + 1;
        }
        else if ((cs.get(identificatore,"nome",getApplicationContext())) != null && identificatore != null){

            id = identificatore;
        }
        else {
            int k = 0;

            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                k++;
            }

            int numero = k + 1;
            id = "Piadina " + numero;
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
        String totaleStringa = df.format(totalePiadina * quantitaPiadina);
        double totaleTroncato = Double.valueOf(totaleStringa);

        cs.add(id,"nome", nomePiadina);
        cs.add(id, "formato", formatoPiadina);
        cs.add(id,"impasto", impastoPiadina);
        cs.add(id,"prezzo", totaleTroncato);
        cs.add(id,"ingredienti", ingredientiPiadina.toString());
        cs.add(id, "quantita", quantitaPiadina);
        cs.add(id, "rating", ratingPiadina);
        cs.add(id,"identifier", id);
        cs.commit(getApplicationContext());
    }

    private void editItemCarrello(){
        String idPiadina = cartItem.getIdentifier();

        cs.update(idPiadina, "nome", nomePiadina, mContext);
        cs.update(idPiadina, "formato", formatoPiadina, mContext);
        cs.update(idPiadina, "impasto", impastoPiadina, mContext);
        cs.update(idPiadina, "prezzo", prezzoPiadinaSingola * quantitaPiadina, mContext);
        cs.update(idPiadina, "ingredienti", ingredientiPiadina.toString(), mContext);
        cs.update(idPiadina, "quantita", quantitaPiadina, mContext);
        cs.update(idPiadina, "rating", ratingPiadina, mContext);
    }

    public void onFragmentInteraction(Uri uri){}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        savedInstanceState.putParcelableArrayList("ingredienti", adapter.getIngredientiArrayList());
        savedInstanceState.putDouble("prezzo", totalePiadina);
        /*Log.d("SAVE", "Telefono girato SAVE!");
        Log.d("SAVE", printIngredienti(adapter.getIngredientiArrayList()));*/

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Log.d("LOAD", "Elementi recuperati!");

        // Recupero ingredienti Piadina: viene fatto in onCreate

        // Recupero prezzo Piadina
        totalePiadina = savedInstanceState.getDouble("prezzo");
        TextView prezzoPiadina = findViewById(R.id.prezzoTotalePiadina);

        BigDecimal totale = new BigDecimal(totalePiadina);
        prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
    }

    private void setRadioButtons(Piadina piadina){
        String formato = piadina.getFormato();
        String impasto = piadina.getImpasto();
        double prezzoFormato;
        double prezzoImpasto;

        RadioGroup radioFormato = findViewById(R.id.rg1);
        RadioGroup radioImpasto = findViewById(R.id.rg2);

        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_rotolo);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_baby);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_integrale);

        switch (formato){
            case "Piadina":
                radioFormato.check(R.id.rb_normale);
                rb1.setTypeface(null, Typeface.BOLD_ITALIC);
                prezzoFormato = 2.5;
                break;
            case "Rotolo":
                radioFormato.check(R.id.rb_rotolo);
                rb2.setTypeface(null, Typeface.BOLD_ITALIC);
                prezzoFormato =  + FORMATO_ROTOLO;
                break;
            case "Baby":
                radioFormato.check(R.id.rb_baby);
                rb3.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            default:
                radioFormato.check(R.id.rb_normale);
                rb1.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
        }

        switch (impasto){
            case "Normale":
                radioImpasto.check(R.id.rb_impasto_normale);
                rb4.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            case "Integrale":
                radioImpasto.check(R.id.rb_integrale);
                rb5.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
            default :
                radioImpasto.check(R.id.rb_impasto_normale);
                rb4.setTypeface(null, Typeface.BOLD_ITALIC);
                break;
        }
    }

    public double getTotaleImpastoEFormato(){
        return totaleImpastoEFormato;
    }

    public double getTotalePiadina(){
        return totalePiadina;
    }

    public void setTotalePiadina(double nuovoTotale){
        totalePiadina = nuovoTotale;

        BigDecimal totale = new BigDecimal(totalePiadina);
        totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);

        prezzoPiadina.setText(totale.toPlainString().replace(".", ",") + " €");
    }

    public void setTotaleIngredienti(double nuovoTotale){
        totaleIngredienti = nuovoTotale;
    }

    public double getTotaleIngredienti(){ return totaleIngredienti; }

}
