package com.example.ale.piadinapp.home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.HomeActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.DBHelper;

import java.util.ArrayList;

public class CustomizePiadinaActivity extends AppCompatActivity
        implements TabMenu.OnFragmentInteractionListener, IngredientsAdapter.ItemClickListener {

    Piadina chosenPiadina;
    IngredientsAdapter adapter;
    DBHelper helper;
    ArrayList<Ingrediente> ingredienti;

    public final static Double IMPASTO_INTEGRALE = 0.30;
    public final static Double FORMATO_BABY = -1.0;
    public final static Double FORMATO_ROTOLO = 2.0;

    static double totale = 0;


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
        double prezzoPiadinaBase = chosenPiadina.getPrice();

        TextView prezzoPiadina = (TextView)findViewById(R.id.prezzoTotalePiadina);
        prezzoPiadina.setText(prezzoPiadinaBase + "€");

        TextView nomePiadina = findViewById(R.id.nome_piadina);
        nomePiadina.setText(chosenPiadina.getNome());
        nomePiadina.setTypeface(null, Typeface.BOLD);

        ingredienti = chosenPiadina.getIngredienti();


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.ingredients);
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
                                TextView prezzoPiadina = (TextView)findViewById(R.id.prezzoTotalePiadina);
                                Double prezzoCorrente = Double.valueOf(removeLastChar(prezzoPiadina.getText().toString()));
                                Double nuovoPrezzo = prezzoCorrente-prezzoIngrediente;
                                String newPrice = nuovoPrezzo.toString();
                                prezzoPiadina.setText(newPrice);
                                adapter.removeItem(position);
                                Toast.makeText(CustomizePiadinaActivity.this, "Ingrediente rimosso", Toast.LENGTH_LONG).show();

                                if(adapter.getItemCount() == 0){

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


    public void onRadioButtonClicked(View v)
    {

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

            totale = prezzoPiadinaBase;
            prezzoPiadina.setText(totale + " €");

        }
        else if (rb2.isChecked() && rb4.isChecked()){

        rb2.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);
        //set the other two radio buttons text style to default
        rb1.setTypeface(null, Typeface.NORMAL);
        rb5.setTypeface(null, Typeface.NORMAL);
        rb3.setTypeface(null, Typeface.NORMAL);

        totale=prezzoPiadinaBase+FORMATO_ROTOLO;
        prezzoPiadina.setText(totale+"€");
        }
        else if (rb3.isChecked() && rb4.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb4.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb5.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            totale = prezzoPiadinaBase + FORMATO_BABY;
            prezzoPiadina.setText(totale+"€");
        }

        else if (rb1.isChecked() && rb5.isChecked()){

            rb1.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb3.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb2.setTypeface(null, Typeface.NORMAL);

            totale = prezzoPiadinaBase + IMPASTO_INTEGRALE;
            prezzoPiadina.setText(totale + " €");
        }

        else if (rb2.isChecked() && rb5.isChecked()){

            rb2.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);
            //set the other two radio buttons text style to default
            rb1.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);
            rb3.setTypeface(null, Typeface.NORMAL);

            totale = prezzoPiadinaBase + FORMATO_ROTOLO+IMPASTO_INTEGRALE;
            prezzoPiadina.setText(totale + " €");
        }

        else if (rb3.isChecked() && rb5.isChecked()){

            rb3.setTypeface(null, Typeface.BOLD_ITALIC);
            rb5.setTypeface(null, Typeface.BOLD_ITALIC);

            rb1.setTypeface(null, Typeface.NORMAL);

            rb2.setTypeface(null, Typeface.NORMAL);
            rb4.setTypeface(null, Typeface.NORMAL);

            totale = prezzoPiadinaBase + FORMATO_BABY+IMPASTO_INTEGRALE;
            prezzoPiadina.setText(totale + " €");
        }


    }

    private static String removeLastChar(String str) {

        return str.substring(0, str.length() - 1);
    }


    public void onFragmentInteraction(Uri uri){}


}
