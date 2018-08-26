package com.example.android.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.R;
import com.example.android.classi.Piadina;
import com.example.android.fragments.TabLeMiePiadine;
import com.example.android.home.ClickListener;
import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class LeMiePiadineAdapter extends RecyclerView.Adapter<LeMiePiadineAdapter.PiadinaViewHolder> {


    //this context we will use to inflate the layout
    private Context mContext;
    private DBHelper helper;
    private OnlineHelper onlineHelper;
    TabLeMiePiadine fragmentPiadine;
    private int nuovoVoto;
    GenericCallback callback;
    Map<String, String> utente;
    

    //we are storing all the products in a list
    private List<Piadina> piadinaList;

    //getting the context and product list with constructor
    public LeMiePiadineAdapter(Context mContext, List<Piadina> piadinaList, TabLeMiePiadine fragmentPiadine) {
        this.mContext = mContext;
        this.piadinaList = piadinaList;
        this.fragmentPiadine = fragmentPiadine;
    }

    @Override
    public PiadinaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        helper = new DBHelper(mContext);
        onlineHelper = new OnlineHelper(mContext);

        View view = inflater.inflate(R.layout.layout_la_mia_piadina, null);
        return new PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PiadinaViewHolder holder, int position) {
        final Piadina piadina = piadinaList.get(position);

        holder.textViewTitle.setText(piadina.getNome());
        holder.impastoPiadina.setText(piadina.getImpasto());
        if(piadina.getImpasto() == "Normale"){

        }
        holder.formatoPiadina.setText(piadina.getFormato());
        holder.textViewIngredients.setText(piadina.printIngredienti());
        holder.textViewPrezzo.setText(String.valueOf(piadina.getPrice()));
        holder.ratingBar.setRating(piadina.getRating());
        holder.ratingBar.setIsIndicator(true);

        callback = new GenericCallback() {
            @Override
            public void onSuccess(JSONObject resultData) {
                boolean success = JSONHelper.getSuccessResponseValue(resultData);
                if(success){
                    helper.updateMiePiadineByID(piadina.getIdEsterno(), nuovoVoto);
                }else{
                    Snackbar.make(holder.itemView, "Errore nel salvataggio della piadina", Snackbar.LENGTH_LONG).show();
                }
            }
        };

        holder.rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.alert_rating, null);
                dialogBuilder.setView(dialogView);

                final RatingBar ratingBar = dialogView.findViewById(R.id.rating_piadina);
                ratingBar.setRating(piadina.getRating());

                dialogBuilder.setTitle("Vota la piadina!");
                dialogBuilder.setIcon(R.drawable.ic_stars_black_24dp);
                dialogBuilder.setMessage("È data la possibilità di votare la piadina per poterla ritrovare più facilamente.");

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        nuovoVoto = (int) rating;
                        piadina.setRating(nuovoVoto);
                        ratingBar.setRating(rating);
                        holder.ratingBar.setRating(rating);

                        // Aggiorno il voto della piadina nel DB esterno e poi interno.
                        onlineHelper.updateRatedPiadinaInExternalDB(piadina, nuovoVoto, callback);

                        // todo: non funziona
                        fragmentPiadine.riordinaClassifica();

                        Snackbar.make(holder.itemView, "Hai modificato il voto alla piadina", Snackbar.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });

        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, CustomizePiadinaActivity.class);
                intent.putExtra("indexPiadina",position);
                (intent);*/
                Toast.makeText(mContext, "Redirect da fare", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(holder.buttonsPiadina.getVisibility()){
                    case View.GONE:
                        holder.buttonsPiadina.setVisibility(View.VISIBLE);
                        holder.buttonsPiadina.animate().alpha(1.0f);
                        break;
                    case View.VISIBLE:
                        holder.buttonsPiadina.setVisibility(View.GONE);
                        holder.buttonsPiadina.animate().alpha(0.0f);
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return piadinaList.size();
    }


    class PiadinaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewIngredients, textViewPrezzo;
        TextView formatoPiadina, impastoPiadina;
        Button orderButton;
        Button rateButton;
        Button impastoButton, formatoButton;
        RatingBar ratingBar;
        LinearLayout buttonsPiadina;
        private WeakReference<ClickListener> listenerRef;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nome_la_mia_piadina);
            formatoPiadina = itemView.findViewById(R.id.formato_la_mia_piadina);
            impastoPiadina = itemView.findViewById(R.id.impasto_la_mia_piadina);
            textViewIngredients = itemView.findViewById(R.id.descrizione_la_mia_piadina);
            textViewPrezzo = itemView.findViewById(R.id.prezzo_la_mia_piadina);
            ratingBar = itemView.findViewById(R.id.voto_la_mia_piadina);

            rateButton = itemView.findViewById(R.id.button_vota);
            orderButton = itemView.findViewById(R.id.button_ordina_la_mia_piadina);
            buttonsPiadina = itemView.findViewById(R.id.layout_button_le_mie_piadine);

        }
    }
}
