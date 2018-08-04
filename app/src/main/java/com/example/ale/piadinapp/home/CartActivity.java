package com.example.ale.piadinapp.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Piadina;

import java.util.Map;

public class CartActivity extends AppCompatActivity {

    Carteasy cs = new Carteasy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*Carteasy cs = new Carteasy();
        String formato = cs.getString("Piadina 1","formato",getApplicationContext());
        String impasto= cs.getString("Piadina 1","impasto",getApplicationContext());

        Map<Integer, Map> data;
        data = cs.ViewAll(getApplicationContext());
        int i=0;
        for (Map.Entry<Integer, Map> entry : data.entrySet()) {
            i++;
        }

        Log.d("TAG","formato"+formato+"impasto"+impasto+i);
*/




    }

}
