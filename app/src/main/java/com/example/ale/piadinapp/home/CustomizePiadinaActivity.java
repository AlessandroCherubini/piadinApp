package com.example.ale.piadinapp.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    public final static Double FORMATO_ROTOLO = 1.0;

    static double impasto=0;
    static double formato=0;
    static double totale=0;


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
        prezzoPiadina.setText(prezzoPiadinaBase+"€");

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

        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);


        rb1.setTypeface(null, Typeface.BOLD_ITALIC);
        rb4.setTypeface(null, Typeface.BOLD_ITALIC);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    public void onRadioButtonClicked(View v)
    {

        helper = new DBHelper(this);
        Intent intent = getIntent();
        int position = intent.getIntExtra("indexPiadina",0);
        chosenPiadina = helper.getPiadinaByPosition((long)position+1);
        double prezzoPiadinaBase = chosenPiadina.getPrice();


        TextView prezzoPiadina = (TextView)findViewById(R.id.prezzoTotalePiadina);

        String prezzoCorrente = removeLastChar(prezzoPiadina.getText().toString());


        RadioButton rb1 = (RadioButton) findViewById(R.id.rb_normale);
        RadioButton rb2 = (RadioButton) findViewById(R.id.rb_rotolo);
        RadioButton rb3 = (RadioButton) findViewById(R.id.rb_baby);
        RadioButton rb4 = (RadioButton) findViewById(R.id.rb_impasto_normale);
        RadioButton rb5 = (RadioButton) findViewById(R.id.rb_integrale);

        //is the current radio button now checked?
        boolean  checked = ((RadioButton) v).isChecked();

        //now check which radio button is selected
        //android switch statement
        switch(v.getId()){

            case R.id.rb_normale:
                if(checked)

                    rb1.setTypeface(null, Typeface.BOLD_ITALIC);
                //set the other two radio buttons text style to default
                rb2.setTypeface(null, Typeface.NORMAL);
                // reqire to import Typeface class
                rb3.setTypeface(null, Typeface.NORMAL);

                totale = impasto + formato + prezzoPiadinaBase;
                prezzoPiadina.setText(totale+"€");

                break;

            case R.id.rb_rotolo:
                if(checked)

                    rb2.setTypeface(null, Typeface.BOLD_ITALIC);
                //set the other two radio buttons text style to default
                rb1.setTypeface(null, Typeface.NORMAL);
                rb3.setTypeface(null, Typeface.NORMAL);

                totale = impasto + FORMATO_ROTOLO + prezzoPiadinaBase;
                prezzoPiadina.setText(totale+"€");

                break;

            case R.id.rb_baby:
                if(checked)

                    rb3.setTypeface(null, Typeface.BOLD_ITALIC);
                //set the other two radio buttons text style to default
                rb1.setTypeface(null, Typeface.NORMAL);
                rb2.setTypeface(null, Typeface.NORMAL);

                totale = impasto + FORMATO_BABY + prezzoPiadinaBase;
                prezzoPiadina.setText(totale+"€");

                break;

            case R.id.rb_impasto_normale:
                if(checked)

                    rb4.setTypeface(null, Typeface.BOLD_ITALIC);
                //set the other two radio buttons text style to default
                rb5.setTypeface(null, Typeface.NORMAL);


                totalep = Double.parseDouble(prezzoCorrente)-IMPASTO_INTEGRALE;
                prezzoPiadina.setText(totale+"€");

                break;

            case R.id.rb_integrale:
                if(checked)

                    rb5.setTypeface(null, Typeface.BOLD_ITALIC);
                //set the other two radio buttons text style to default
                rb4.setTypeface(null, Typeface.NORMAL);

                totale=Double.parseDouble(prezzoCorrente)+IMPASTO_INTEGRALE;

                prezzoPiadina.setText(totale+"€");

                break;
        }
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }


    public void onFragmentInteraction(Uri uri){}


}
