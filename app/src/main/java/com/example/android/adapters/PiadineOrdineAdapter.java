package com.example.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.classi.Piadina;

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
        String ingredientiToString = piadina.printIngredienti();

        holder.nomePiadina.setText(piadina.getNome());
        holder.formatoPiadina.setText(piadina.getFormato());
        holder.impastoPiadina.setText(piadina.getImpasto());
        holder.descrizionePiadina.setText(ingredientiToString);
        holder.prezzoPiadina.setText(String.valueOf(piadina.getPrice()));
        holder.quantitaPiadina.setText(String.valueOf(piadina.getQuantita()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nomePiadina;
        TextView descrizionePiadina;
        TextView prezzoPiadina;
        TextView quantitaPiadina;
        TextView formatoPiadina;
        TextView impastoPiadina;

        public ViewHolder(View itemView) {
            super(itemView);

            nomePiadina = itemView.findViewById(R.id.nome_piadina_ordine);
            formatoPiadina = itemView.findViewById(R.id.formato_piadina_ordine);
            impastoPiadina = itemView.findViewById(R.id.impasto_piadina_ordine);
            descrizionePiadina = itemView.findViewById(R.id.descrizione_piadina_ordine);
            prezzoPiadina = itemView.findViewById(R.id.prezzo_piadina_ordine);
            quantitaPiadina = itemView.findViewById(R.id.quantita_ordine);

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
