package com.example.ale.piadinapp.home;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.MainActivity;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {


    CartItemAdapter adapter;
    ArrayList<CartItem> Items = new ArrayList<CartItem>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<Ingrediente>();
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //cs.persistData(getApplicationContext(),true);

        // ricevo l'elemento inserito nel carrello

        data = cs.ViewAll(getApplicationContext());
        String id;
        if (data==null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {

                int numero = k+1;
                id="Piadina "+numero;
                String formato= cs.getString(id, "formato", getApplicationContext());
                String impasto =cs.getString(id, "impasto", getApplicationContext());
                String ingredients = cs.getString(id, "ingredienti", getApplicationContext());
//                Double prezzo= cs.getDouble(id, "prezzo", getApplicationContext());
                String nome = cs.getString(id, "nome",getApplicationContext());

                //ricostruisco gli ingredienti e l'stanza della classe CartItem

                String strippedIngredients= ingredients.replaceAll("\\[", "").replaceAll("\\]","");
                List<String> ings = Arrays.asList(strippedIngredients.split(","));

                ingredienti = new ArrayList<>();
                ingredienti.clear();

                for(String ing : ings)
                {

                    ingredienti.add(new Ingrediente(ing));}

                CartItem item = new CartItem(nome,formato, impasto,5.0,ingredienti);
                Items.add(item);

                k++;

            }
        }



        final RecyclerView rv = findViewById(R.id.cart_item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(getApplicationContext(), Items, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }
        });

        rv.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CartActivity.this, R.drawable.piadina_divider));
        rv.addItemDecoration(itemDecorator);











    }

}
