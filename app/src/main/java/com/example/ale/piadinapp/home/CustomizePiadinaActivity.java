package com.example.ale.piadinapp.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.CustomAdapter;
import com.example.ale.utility.DBHelper;

import java.util.ArrayList;

public class CustomizePiadinaActivity extends AppCompatActivity
        implements TabMenu.OnFragmentInteractionListener, IngredientsAdapter.ItemClickListener{

    Piadina chosenPiadina;
    IngredientsAdapter adapter;
    DBHelper helper;
    ArrayList<Ingrediente> ingredienti;
    Color spinnerColor = new Color ();

    public final static Double IMPASTO_INTEGRALE = 0.30;
    public final static Double FORMATO_BABY = -1.0;
    public final static Double FORMATO_ROTOLO = 2.0;

    static double totalePiadina = 0;
    static double totaleImpastoEFormato;
    static double totaleIngredienti = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_piadina);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // uso l'extra per prendere la piadina selezionata
        helper = new DBHelper(this);
        Intent intent = getIntent();
        int position = intent.getIntExtra("indexPiadina",0);
        chosenPiadina = helper.getPiadinaByPosition((long)position+1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totaleImpastoEFormato = chosenPiadina.getPrice();
        totalePiadina = totaleImpastoEFormato;

        final TextView prezzoPiadina = (TextView)findViewById(R.id.prezzoTotalePiadina);
        prezzoPiadina.setText(totalePiadina+ " €");

        TextView nomePiadina = findViewById(R.id.nome_piadina);
        nomePiadina.setText(chosenPiadina.getNome());
        nomePiadina.setTypeface(null, Typeface.BOLD);

        ingredienti = chosenPiadina.getIngredienti();


        // set up the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientsAdapter(this,ingredienti);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CustomizePiadinaActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CustomizePiadinaActivity.this, R.drawable.piadina_divider));
        recyclerView.addItemDecoration(itemDecorator);

        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);


        rb1.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);

        final Button button = findViewById(R.id.addKart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Piadina aggiunta al carrello", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        });

        ArrayList<String> nomiIngredienti = helper.getNomiIngredienti();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_ingredienti);
        // Create an ArrayAdapter using the string array and a default spinner layout
        CustomAdapter dataAdapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, nomiIngredienti,
                0);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    Object item = parent.getItemAtPosition(position);
                    Ingrediente ingredienteDaAggiungere = helper.getIngredienteByName(item.toString());
                    ingredienti.add(ingredienteDaAggiungere);
                    adapter.notifyItemInserted(ingredienti.size() - 1);
                    double prezzoIngrediente = ingredienteDaAggiungere.getPrice();
                    totalePiadina = totalePiadina + prezzoIngrediente;
                    totaleIngredienti = totaleIngredienti + prezzoIngrediente;
                    prezzoPiadina.setText(totalePiadina + " €");

                    parent.setSelection(0);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                                prezzoPiadina.setText(totalePiadina + " €");
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
                builder.setMessage("Vuoi rimuovere " + adapter.getItem(position).getName() + " ?").setPositiveButton("Sì", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                break;

        }


        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    public void onRadioButtonClicked(View v) {

        helper = new DBHelper(this);
        Intent intent = getIntent();
        int position = intent.getIntExtra("indexPiadina",0);
        chosenPiadina = helper.getPiadinaByPosition((long)position + 1);
        double prezzoPiadinaBase = chosenPiadina.getPrice();


        TextView prezzoPiadina = findViewById(R.id.prezzoTotalePiadina);
        //String prezzoCorrente = removeLastChar(prezzoPiadina.getText().toString());


        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_rotolo);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_baby);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_integrale);

        //is the current radio button now checked?
        boolean  checked = ((RadioButton) v).isChecked();

        //now check which radio button is selected
        if (rb1.isChecked() && rb4.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb2.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            totaleImpastoEFormato = prezzoPiadinaBase;
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            prezzoPiadina.setText(totalePiadina + " €");

        }
        else if (rb2.isChecked() && rb4.isChecked()){

        rb2.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);
        //set the other two radio buttons text style to default
        rb1.setTypeface(null, Typeface.NORMAL);
        rb5.setTypeface(null, Typeface.NORMAL);
        rb3.setTypeface(null, Typeface.NORMAL);

        totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO;
        totalePiadina = totaleImpastoEFormato + totaleIngredienti;
        prezzoPiadina.setText(totalePiadina+ " €");
        }
        else if (rb3.isChecked() && rb4.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY;
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            prezzoPiadina.setText(totalePiadina+ " €");
        }

        else if (rb1.isChecked() && rb5.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb3.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            totaleImpastoEFormato = prezzoPiadinaBase + IMPASTO_INTEGRALE;
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            prezzoPiadina.setText(totalePiadina+ " €");
        }

        else if (rb2.isChecked() && rb5.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_ROTOLO + IMPASTO_INTEGRALE;
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            prezzoPiadina.setText(totalePiadina+ " €");
        }

        else if (rb3.isChecked() && rb5.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);

            rb1.setTypeface(null, Typeface.NORMAL);

            rb2.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);

            totaleImpastoEFormato = prezzoPiadinaBase + FORMATO_BABY + IMPASTO_INTEGRALE;
            totalePiadina = totaleImpastoEFormato + totaleIngredienti;
            prezzoPiadina.setText(totalePiadina+ " €");
        }


    }

    private static String removeLastChar(String str) {

        return str.substring(0, str.length() - 1);
    }


    public void onFragmentInteraction(Uri uri){}


}
