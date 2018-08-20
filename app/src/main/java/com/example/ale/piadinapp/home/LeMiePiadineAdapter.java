package com.example.ale.piadinapp.home;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Piadina;
import com.example.ale.utility.DBHelper;

import java.lang.ref.WeakReference;
import java.util.List;

public class LeMiePiadineAdapter extends RecyclerView.Adapter<LeMiePiadineAdapter.PiadinaViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;
    private DBHelper helper;

    //we are storing all the products in a list
    private List<Piadina> piadinaList;
    //Listener for buttons
    private final ClickListener listener;

    //getting the context and product list with constructor
    public LeMiePiadineAdapter(Context mCtx, List<Piadina> piadinaList, ClickListener listener ) {
        this.mCtx = mCtx;
        this.listener = listener;
        this.piadinaList = piadinaList;
    }

    @Override
    public PiadinaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        helper = new DBHelper(mCtx);
        View view = inflater.inflate(R.layout.layout_la_mia_piadina, null);
        return new PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PiadinaViewHolder holder, int position) {
        final Piadina piadina = piadinaList.get(position);

        holder.textViewTitle.setText(piadina.getNome());
        holder.impastoPiadina.setText(piadina.getImpasto());
        holder.formatoPiadina.setText(piadina.getFormato());
        holder.textViewIngredients.setText(piadina.printIngredienti());
        holder.textViewPrezzo.setText(String.valueOf(piadina.getPrice()));
        holder.ratingBar.setRating(piadina.getRating());
        // todo: da sistemare il listener: non funziona il click!!!
        holder.ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Click", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mCtx);
                LayoutInflater inflater = (LayoutInflater)mCtx.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_rating, null);
                dialogBuilder.setView(dialogView);

                final RatingBar ratingBar = dialogView.findViewById(R.id.rating_piadina);
                ratingBar.setRating(piadina.getRating());

                dialogBuilder.setTitle("Vota la piadina!");
                dialogBuilder.setIcon(R.drawable.ic_stars_black_24dp);
                dialogBuilder.setMessage("È data la possibilità di votare la piadina per poterla ritrovare più facilamente.");

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        piadina.setRating((int) rating);
                        ratingBar.setRating(rating);
                        // Aggiorno il voto della piadina nel DB interno, così da memorizzarla.
                        helper.updateMiePiadineByID(piadina.getIdEsterno(), (int)rating);
                        // todo: da fare il meccanismo per l'aggiornamento delle piadine votate sul db esterno
                        // todo: pensavo di memorizzare IDESTERNO, VOTO per ogni piadina cambiata e poi quando si chiude la tab
                        // todo: far partire la richiesta di aggiornamento per tutte le piadine memorizzate nel vettore.
                        // todo: (da scrivere ancora in OnlineHelper il metodo per l'update, non c'è neanche il backend)
                        // todo: magari lo faccio io quando torno, ma per lo meno risolviamo il click listener.
                        Toast.makeText(mCtx, "Hai votato!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(holder.orderButton.getVisibility()){
                    case View.GONE:
                        holder.orderButton.setVisibility(View.VISIBLE);
                        holder.orderButton.animate().alpha(1.0f);
                        break;
                    case View.VISIBLE:
                        holder.orderButton.setVisibility(View.GONE);
                        holder.orderButton.animate().alpha(0.0f);
                        break;
                }
            }
        });

    }


    @Override
    public int getItemCount() {

        return piadinaList.size();
    }


    class PiadinaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewIngredients, textViewRating, textViewPrezzo;
        TextView formatoPiadina, impastoPiadina;
        Button orderButton;
        RatingBar ratingBar;
        private WeakReference<ClickListener> listenerRef;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nome_la_mia_piadina);
            formatoPiadina = itemView.findViewById(R.id.formato_la_mia_piadina);
            impastoPiadina = itemView.findViewById(R.id.impasto_la_mia_piadina);
            textViewIngredients = itemView.findViewById(R.id.descrizione_la_mia_piadina);
            textViewPrezzo = itemView.findViewById(R.id.prezzo_la_mia_piadina);
            //rateButton = itemView.findViewById(R.id.button_rate_la_mia_piadina);
            ratingBar = itemView.findViewById(R.id.voto_la_mia_piadina);
            orderButton = itemView.findViewById(R.id.button_ordina_la_mia_piadina);
            listenerRef = new WeakReference<>(listener);

            ratingBar.setOnClickListener(this);
        }

        public void onClick(View v) {
            listenerRef.get().onPositionClicked(v, getAdapterPosition());
        }
    }


}
