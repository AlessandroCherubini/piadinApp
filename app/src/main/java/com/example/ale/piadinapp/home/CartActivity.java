package com.example.ale.piadinapp.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.piadinapp.classi.Piadina;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {


    CartItemAdapter adapter;
    ArrayList<CartItem> Items = new ArrayList<CartItem>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<Ingrediente>();
    String nome;
    Carteasy cs = new Carteasy();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



       /* String formato= cs.getString("Piadina 8", "formato", getApplicationContext());
        String impasto =cs.getString("Piadina 8", "impasto", getApplicationContext());
        //ingredienti = cs.get("Piadina 1", "ingredienti", getApplicationContext());
        Double prezzo= cs.getDouble("Piadina 8", "prezzo", getApplicationContext());
        //nome = cs.getString("Piadina 1", "nome",getApplicationContext());

        Ingrediente pollo;
        ingredienti.add(pollo = new Ingrediente ("pollo"));

        CartItem item1 = new CartItem("cheru",formato, impasto,prezzo,ingredienti);*/
        CartItem item1 = (CartItem)cs.get("Piadina 10", "Piadina",getApplicationContext());
        Items.add(item1);



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






        /*Carteasy cs = new Carteasy();
        String formato = cs.getString("Piadina 1","formato",getApplicationContext());
        String impasto= cs.getString("Piadina 1","impasto",getApplicationContext());

        Map<Integer, Map> data;
        data = cs.ViewAll(getApplicationContext());
        int i=0;
        for (Map.Entry<Integer, Map> entry : data.entrySet()) {
            i++;
        }

    
*/




    }

}
