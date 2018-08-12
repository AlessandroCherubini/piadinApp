package com.example.ale.piadinapp.home;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;


import com.carteasy.v1.lib.Carteasy;
import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity{

    CartItemAdapter adapter;
    ArrayList<CartItem> Items = new ArrayList<CartItem>();
    ArrayList<Ingrediente> ingredienti = new ArrayList<Ingrediente>();
    Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    TextView tvTot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTot = findViewById(R.id.tv_total);
        Button clearCart = findViewById((R.id.clear_cart));

        data = cs.ViewAll(getApplicationContext());

        clearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svuotaCarrello();
                recreate();
            }
        });

        String id;
        if (data == null || data.size()==0) {
            Toast.makeText(getApplicationContext(), "Non ci sono elementi nel carrello", Toast.LENGTH_SHORT).show();
        } else {
            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {

                int numero = k + 1;
                id = "Piadina " + numero;
                String formato = cs.getString(id, "formato", getApplicationContext());
                String impasto = cs.getString(id, "impasto", getApplicationContext());
                String ingredients = cs.getString(id, "ingredienti", getApplicationContext());
                Double prezzo= cs.getDouble(id, "prezzo", getApplicationContext());
                String nome = cs.getString(id, "nome", getApplicationContext());
                String identifier =cs.getString(id,"identifier",getApplicationContext());

                //ricostruisco gli ingredienti e l'stanza della classe CartItem

                String strippedIngredients = ingredients.replaceAll("\\[", "").replaceAll("\\]", "");
                List<String> ings = Arrays.asList(strippedIngredients.split(","));

                ingredienti = new ArrayList<>();
                ingredienti.clear();

                for (String ing : ings) {
                    ingredienti.add(new Ingrediente(ing));
                }

                CartItem item = new CartItem(nome, formato, impasto, prezzo, ingredienti,identifier);
                Items.add(item);

                k++;

            }
        }

        final RecyclerView rv = findViewById(R.id.cart_item);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(getApplicationContext(), Items, new ClickListener() {
            @Override
            public void onPositionClicked(View view, final int position) {
                switch(view.getId()){
                    case R.id.editButton:
                        Intent intent = new Intent(getApplicationContext(),CustomizePiadinaActivity.class);
                        Gson gson = new Gson();
                        String modificaPiadinaAsAString = gson.toJson(adapter.getItem(position));
                        intent.putExtra("modificaPiadina",modificaPiadinaAsAString);
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
                        builder.setMessage("Sei sicuro di volerlo eliminare?").setPositiveButton("Sì", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        break;
                }

            }
        });

        rv.setAdapter(adapter);
        setTotale();

        DividerItemDecoration itemDecorator = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(CartActivity.this, R.drawable.piadina_divider));
        rv.addItemDecoration(itemDecorator);
    }

    public void setTotale(){
        // Somma dei prezzi delle piadine
        double tot = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            tot = tot + adapter.getItem(i).getPrezzo();
        }

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
            Items.clear();
            data = null;
        }
    }
}


