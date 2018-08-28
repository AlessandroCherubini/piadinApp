package com.example.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
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

import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.classi.Piadina;
import com.example.android.fragments.TabLeMiePiadine;
import com.example.android.utility.DBHelper;
import com.example.android.utility.GenericCallback;
import com.example.android.utility.JSONHelper;
import com.example.android.utility.OnlineHelper;
import com.example.android.utility.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class LeMiePiadineNonVotateAdapter extends RecyclerView.Adapter<LeMiePiadineNonVotateAdapter.PiadinaViewHolder>{

    private Context mContext;
    private DBHelper helper;
    private OnlineHelper onlineHelper;
    private GenericCallback callback;
    private ArrayList<Piadina> piadineNonVotate;
    private Map<String, String> utente;
    private TabLeMiePiadine fragmentLeMiePiadine;

    private Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    String identificatore;

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
        onlineHelper = new OnlineHelper(mContext);
        utente = SessionManager.getUserDetails(mContext);

        View view = inflater.inflate(R.layout.layout_piadina_non_votata, null);
        return new LeMiePiadineNonVotateAdapter.PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PiadinaViewHolder holder, final int position) {

        final Piadina piadina = piadineNonVotate.get(position);

        holder.textViewTitle.setText(piadina.getNome());
        holder.impastoPiadina.setText(piadina.getImpasto());

        if(piadina.getFormato().equals("Piadina")){
            holder.iconaFormato.setBackgroundResource(R.drawable.ic_piadina);
        }else if(piadina.getFormato().equals("Rotolo")){
            holder.iconaFormato.setBackgroundResource(R.drawable.ic_rotolo);
        }else{
            holder.iconaFormato.setBackgroundResource(R.drawable.ic_baby);
        }

        if(piadina.getImpasto().equals("Normale")){
            holder.iconaImpasto.setBackgroundResource(R.drawable.ic_impasto_tradizionale);
        }else{
            holder.iconaImpasto.setBackgroundResource(R.drawable.ic_impasto_4cereali);
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
                dialogBuilder.setIcon(R.drawable.ic_stars_brown_24dp);
                dialogBuilder.setMessage("Verrà poi aggiunta ne 'La mia classifica' per ritrovarla velocemente!");

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                callback = new GenericCallback() {
                    @Override
                    public void onSuccess(JSONObject resultData) {
                        boolean success = JSONHelper.getSuccessResponseValue(resultData);
                        if(success){
                            int idEsterno = JSONHelper.getIntFromObj(resultData, "id_esterno");
                            piadina.setIdEsterno(idEsterno);
                            helper.insertPiadinaVotata(piadina, utente.get("email"));
                        }else{
                            Snackbar.make(holder.itemView, "Errore nel salvataggio della piadina", Snackbar.LENGTH_LONG).show();
                        }
                    }
                };

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        piadina.setRating((int) rating);
                        ratingBar.setRating(rating);

                        // Memorizzo la piadina nelle Piadine Votate prima sull'esterno e poi interno.
                        onlineHelper.addRatedPiadinaInExternalDB(piadina, utente.get("email"), callback);

                        fragmentLeMiePiadine.addPiadinaVotataInAdapter(piadina);
                        fragmentLeMiePiadine.removePiadinaNonVotataInAdapter(position);
                        //fragmentLeMiePiadine.setEmptyPiadineNonVotate();
                        fragmentLeMiePiadine.setEmptyMessage();

                        Snackbar.make(holder.itemView, "Hai votato! La piadina ora è in classifica!", Snackbar.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });

        holder.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiAlCarrello(piadineNonVotate.get(position));
                ((Activity) mContext).finish();
                mContext.startActivity(((Activity) mContext).getIntent());
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
        Button iconaFormato, iconaImpasto;

        LinearLayout buttonsPiadina;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nome_la_mia_piadina_non_votata);
            formatoPiadina = itemView.findViewById(R.id.formato_la_mia_piadina_non_votata);
            impastoPiadina = itemView.findViewById(R.id.impasto_la_mia_piadina_non_votata);
            iconaFormato = itemView.findViewById(R.id.icona_formato);
            iconaImpasto = itemView.findViewById(R.id.icona_impasto);
            textViewIngredients = itemView.findViewById(R.id.descrizione_la_mia_piadina_non_votata);
            textViewPrezzo = itemView.findViewById(R.id.prezzo_la_mia_piadina_non_votata);

            rateButton = itemView.findViewById(R.id.button_vota_non_votata);
            orderButton = itemView.findViewById(R.id.button_ordina_la_mia_piadina_non_votata);
            buttonsPiadina = itemView.findViewById(R.id.layout_button_le_mie_piadine_non_votate);

        }
    }

    private void aggiungiAlCarrello(Piadina piadina){

        data = cs.ViewAll(mContext);
        String id;

        if (data == null || data.size() == 0) {
            id = "Piadina " + 1;
        }
        else if ((cs.get(identificatore,"nome", mContext)) != null && identificatore != null){

            id = identificatore;

        }
        else {

            int k = 0;
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {
                k++;
            }

            int numero = k + 1;
            id = "Piadina " + numero;
        }

        cs.add(id,"nome", piadina.getNome());
        cs.add(id, "formato", piadina.getFormato());
        cs.add(id,"impasto", piadina.getImpasto());

        cs.add(id,"prezzo", piadina.getPrice());
        cs.add(id,"ingredienti", piadina.printIngredienti());
        cs.add(id, "quantita", piadina.getQuantita());
        cs.add(id, "rating", piadina.getRating());
        cs.add(id,"identifier", id);
        cs.commit(mContext);

        if(piadina.getQuantita() > 1){
            Toast.makeText(mContext, "Piadine aggiunte al carrello!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext, "Piadina aggiunta al carrello!", Toast.LENGTH_SHORT).show();
        }

    }
}
