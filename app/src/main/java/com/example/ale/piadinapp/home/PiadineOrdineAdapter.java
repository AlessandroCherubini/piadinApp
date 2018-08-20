package com.example.ale.piadinapp.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Piadina;

import java.util.ArrayList;

public class PiadineOrdineAdapter extends RecyclerView.Adapter<PiadineOrdineAdapter.ViewHolder> {

    private ArrayList<Piadina> mData;
    private LayoutInflater mInflater;
    private PiadineOrdineAdapter.PiadineOrdineListener mClickListener;
    private Context mContext;

    public PiadineOrdineAdapter(Context context, ArrayList<Piadina> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public PiadineOrdineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_piadina_ordine, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Piadina piadina = mData.get(position);

        holder.nomePiadina.setText(piadina.getNome());
        holder.descrizionePiadina.setText(piadina.getFormato());
        holder.prezzoPiadina.setText(String.valueOf(piadina.getPrice()));

        holder.buttonVotaPiadina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
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
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nomePiadina;
        TextView descrizionePiadina;
        TextView prezzoPiadina;
        Button buttonVotaPiadina;

        public ViewHolder(View itemView) {
            super(itemView);

            nomePiadina = itemView.findViewById(R.id.nome_piadina_ordine);
            descrizionePiadina = itemView.findViewById(R.id.descrizione_piadina_ordine);
            prezzoPiadina = itemView.findViewById(R.id.prezzo_piadina_ordine);
            buttonVotaPiadina = itemView.findViewById(R.id.button_rate_piadina_ordine);

            buttonVotaPiadina.setOnClickListener(this);

        }

        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

        }
    }
        // allows clicks events to be caught
    public void setClickListener(PiadineOrdineAdapter.PiadineOrdineListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface PiadineOrdineListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
