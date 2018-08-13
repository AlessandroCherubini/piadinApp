package com.example.ale.piadinapp.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.DBHelper;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class CustomizePiadinaActivity extends AppCompatActivity
        implements TabMenu.OnFragmentInteractionListener, IngredientsAdapter.ItemClickListener{

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

    static double totalePiadina = 0;
    static double totaleImpastoEFormato = 0;
    static double totaleIngredienti = 0;
    public Context mContext;
    String identificatore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_piadina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = CustomizePiadinaActivity.this;

        // uso l'extra per prendere la piadina selezionata
        helper = new DBHelper(this);
        Intent intent = getIntent();
        TextView name = findViewById(R.id.nome_piadina);

        if (intent.getExtras().get("indexPiadina")!= null){

            int position = intent.getIntExtra("indexPiadina",0);
            chosenPiadina = helper.getPiadinaByPosition((long)position+1);
        }
        else if (intent.getExtras().get("randomPiadina")!= null){
            Gson gson = new Gson();
            String chosenPiadinaString = getIntent().getStringExtra("randomPiadina");
            chosenPiadina = gson.fromJson(chosenPiadinaString, Piadina.class);
        }
        else if (intent.getExtras().get("modificaPiadina")!= null){
            Gson gson = new Gson();
            String chosenPiadinaString = getIntent().getStringExtra("modificaPiadina");
            cartItem = gson.fromJson(chosenPiadinaString, CartItem.class);
            chosenPiadina = cartItem.cartItemToPiadina();
            // Set dei radio button per Formato e Impasto
            setRadioButtons(chosenPiadina);

            Button editButton = findViewById(R.id.addKart);
            editButton.setText("Conferma Modifica");
            editButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_edit_black_24dp, 0, 0, 0);

            //TODO: sistemare problemi

            if (cartItem.getFormato().equalsIgnoreCase("Rotolo")){

                rb2.setChecked(true);
                rb2.setTypeface(null, Typeface.BOLD_ITALIC);

            }
            else if(cartItem.getFormato().equalsIgnoreCase("Baby")){

                rb3.setChecked(true);
                rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            }

            if (cartItem.getImpasto().equalsIgnoreCase("Integrale")){

                rb5.setChecked(true);
                rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            }

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Attributi variabili della piadina selezionata
        nomePiadina = chosenPiadina.getNome();
        formatoPiadina = chosenPiadina.getFormato();
        impastoPiadina = chosenPiadina.getImpasto();
        quantitaPiadina = chosenPiadina.getQuantita();
        ratingPiadina = chosenPiadina.getRating();
        totaleImpastoEFormato = chosenPiadina.getPrice();
        totalePiadina = totaleImpastoEFormato;

        final TextView prezzoPiadina = findViewById(R.id.prezzoTotalePiadina);
        prezzoPiadina.setText(totalePiadina + " €");

        TextView nomePiadina = findViewById(R.id.nome_piadina);
        nomePiadina.setText(chosenPiadina.getNome());
        nomePiadina.setTypeface(null, Typeface.BOLD);

        // Radio Button
        if (rb1.isChecked()){rb1.setTypeface(null, Typeface.BOLD_ITALIC);}
        if(rb4.isChecked()){rb4.setTypeface(null, Typeface.BOLD_ITALIC);}


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Piadina aggiunta al carrello", Toast.LENGTH_SHORT);
                toast.show();
                aggiungiAlCarrello();
                finish();
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

        categorieAdapter.setClickListener(new CategorieIngredientiAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(CustomizePiadinaActivity.this,"Cliccato: " + categorieAdapter.getItem(position), Toast.LENGTH_SHORT).show();

                }
        });
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
                                prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");

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


        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;

            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
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
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;

            BigDecimal totale = new BigDecimal(totalePiadina);
            prezzoPiadina.setText(totale.setScale(2,BigDecimal.ROUND_HALF_EVEN).toPlainString() + " €");
        }

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

        cs.add(id,"nome", nomePiadina);
        cs.add(id, "formato", formatoPiadina);
        cs.add(id,"impasto", impastoPiadina);
        cs.add(id,"prezzo", totalePiadina);
        cs.add(id,"ingredienti", ingredientiPiadina.toString());
        cs.add(id, "quantita", quantitaPiadina);
        cs.add(id, "rating", ratingPiadina);
        cs.add(id,"identifier", id);
        cs.commit(getApplicationContext());
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

        RadioGroup radioFormato = findViewById(R.id.rg1);
        RadioGroup radioImpasto = findViewById(R.id.rg2);

        switch (formato){
            case "Piadina":
                radioFormato.check(R.id.rb_normale);
                break;
            case "Rotolo":
                radioFormato.check(R.id.rb_rotolo);
                break;
            case "Baby":
                radioFormato.check(R.id.rb_baby);
                break;
            default:
                radioFormato.check(R.id.rb_normale);
                break;
        }

        switch (impasto){
            case "Normale":
                radioImpasto.check(R.id.rb_impasto_normale);
                break;
            case "Integrale":
                radioImpasto.check(R.id.rb_integrale);
                break;
            default :
                radioImpasto.check(R.id.rb_impasto_normale);
                break;
        }
    }
    // Funzione utilizzata per stampare gli ingredienti da ArrayList a String per i log.
/*    private String printIngredienti(ArrayList<Ingrediente> ingredienti){
        String ingredientiStringa="";

        for(Ingrediente ingrediente:ingredienti){
            String parziale;

            parziale = ingrediente.getIdIngrediente() + ingrediente.getName() + ingrediente.getPrice() +
                    ingrediente.getCategoria() + ingrediente.getLastUpdated();

            ingredientiStringa = ingredientiStringa + " " + parziale;
        }
        return ingredientiStringa;
    }*/
}
