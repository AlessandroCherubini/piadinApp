package com.example.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.example.android.utility.DBHelper;
import com.example.android.utility.SessionManager;

import java.util.ArrayList;
import java.util.Map;

public class LeMiePiadineNonVotateAdapter extends RecyclerView.Adapter<LeMiePiadineNonVotateAdapter.PiadinaViewHolder>{

    private Context mContext;
    private DBHelper helper;
    private ArrayList<Piadina> piadineNonVotate;
    private Map<String, String> utente;
    private TabLeMiePiadine fragmentLeMiePiadine;

    public LeMiePiadineNonVotateAdapter(Context context, ArrayList<Piadina> piadineNonVotate, TabLeMiePiadine fragmentLeMiePiadine){
        this.mContext = context;
        this.piadineNonVotate = piadineNonVotate;
        this.fragmentLeMiePiadine = fragmentLeMiePiadine;
    }

    @NonNull
    @Override
    public PiadinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        helper = new DBHelper(mContext);
        utente = SessionManager.getUserDetails(mContext);

        View view = inflater.inflate(R.layout.layout_piadina_non_votata, null);
        return new LeMiePiadineNonVotateAdapter.PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PiadinaViewHolder holder, final int position) {

        final Piadina piadina = piadineNonVotate.get(position);

        holder.textViewTitle.setText(piadina.getNome());
        holder.impastoPiadina.setText(piadina.getImpasto());
        if(piadina.getImpasto() == "Normale"){

        }
        holder.formatoPiadina.setText(piadina.getFormato());
        holder.textViewIngredients.setText(piadina.printIngredienti());
        holder.textViewPrezzo.setText(String.valueOf(piadina.getPrice()));

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

        holder.rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.alert_rating, null);
                dialogBuilder.setView(dialogView);

                final RatingBar ratingBar = dialogView.findViewById(R.id.rating_piadina);
                ratingBar.setRating(piadina.getRating());

                dialogBuilder.setTitle("Vota la piadina!");
                dialogBuilder.setIcon(R.drawable.ic_stars_black_24dp);
                dialogBuilder.setMessage("Verrà poi aggiunta ne 'La mia classifica' per ritrovarla velocemente!");

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        piadina.setRating((int) rating);
                        ratingBar.setRating(rating);

                        // Memorizzo la piadina nelle Piadine Votate!
                        helper.insertPiadinaVotata(piadina, utente.get("email"));
                        fragmentLeMiePiadine.addPiadinaVotataInAdapter(piadina);
                        fragmentLeMiePiadine.removePiadinaNonVotataInAdapter(position);
                        fragmentLeMiePiadine.setEmptyMessage();

                        Toast.makeText(mContext, "Hai votato!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return piadineNonVotate.size();
    }

    public class PiadinaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewIngredients, textViewPrezzo;
        TextView formatoPiadina, impastoPiadina;
        Button orderButton;
        Button rateButton;
        Button impastoButton, formatoButton;

        LinearLayout buttonsPiadina;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nome_la_mia_piadina_non_votata);
            formatoPiadina = itemView.findViewById(R.id.formato_la_mia_piadina_non_votata);
            impastoPiadina = itemView.findViewById(R.id.impasto_la_mia_piadina_non_votata);
            textViewIngredients = itemView.findViewById(R.id.descrizione_la_mia_piadina_non_votata);
            textViewPrezzo = itemView.findViewById(R.id.prezzo_la_mia_piadina_non_votata);

            rateButton = itemView.findViewById(R.id.button_vota_non_votata);
            orderButton = itemView.findViewById(R.id.button_ordina_la_mia_piadina_non_votata);
            buttonsPiadina = itemView.findViewById(R.id.layout_button_le_mie_piadine_non_votate);

        }
    }
}
