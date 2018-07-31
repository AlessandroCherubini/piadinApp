package com.example.ale.piadinapp.home;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.DBHelper;

import java.util.ArrayList;

public class CustomizePiadinaActivity extends AppCompatActivity
        implements TabMenu.OnFragmentInteractionListener {

    Piadina chosenPiadina;
    DBHelper helper;

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

        TextView nomePiadina = findViewById(R.id.nome_piadina);
        nomePiadina.setText(chosenPiadina.getNome());
        nomePiadina.setTypeface(null, Typeface.BOLD);
    }

    public void onFragmentInteraction(Uri uri){}


}
