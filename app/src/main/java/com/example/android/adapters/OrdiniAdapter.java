package com.example.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;

import java.util.ArrayList;


public class OrdiniAdapter extends RecyclerView.Adapter<OrdiniAdapter.ViewHolder>{

    private ArrayList<Ordine> mData;
    private LayoutInflater mInflater;
    private OrdineListener mClickListener;
    private Context mContext;
    RelativeLayout layoutDettagli;

    public OrdiniAdapter(Context context, ArrayList<Ordine> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_ordine, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Ordine ordine = mData.get(position);
        holder.dataOrdine.setText(ordine.getTimestampOrdine());
        holder.totaleOrdine.setText(String.valueOf(ordine.getPrezzoOrdine()));

        holder.recyclerViewPiadine.setHasFixedSize(true);
        holder.recyclerViewPiadine.setLayoutManager(new LinearLayoutManager(mContext));

        ArrayList<Piadina> piadineOrdine = ordine.getCartItems();
        PiadineOrdineAdapter adapterPiadine = new PiadineOrdineAdapter(mContext, piadineOrdine);
        holder.recyclerViewPiadine.setAdapter(adapterPiadine);

        holder.buttonDettagliOrdine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.layoutDettagliOrdine.getVisibility() == View.VISIBLE){
                    holder.layoutDettagliOrdine.animate().alpha(0.0f);
                    holder.layoutDettagliOrdine.setVisibility(View.GONE);
                }else{
                    holder.layoutDettagliOrdine.setVisibility(View.VISIBLE);
                    holder.layoutDettagliOrdine.animate().alpha(1.0f);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dataOrdine;
        TextView totaleOrdine;
        Button buttonRiOrdina;
        Button buttonDettagliOrdine;
        RecyclerView recyclerViewPiadine;
        RelativeLayout layoutDettagliOrdine;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dataOrdine = itemView.findViewById(R.id.data_ordine);
            totaleOrdine = itemView.findViewById(R.id.prezzo_piadine_ordine);
            buttonDettagliOrdine = itemView.findViewById(R.id.button_dettagli_ordine);
            buttonDettagliOrdine.setOnClickListener(this);
            buttonRiOrdina = itemView.findViewById(R.id.button_ri_ordina);
            layoutDettagli = itemView.findViewById(R.id.layout_dettagli);
            layoutDettagliOrdine = itemView.findViewById(R.id.layout_dettagli_piadine);


            recyclerViewPiadine = itemView.findViewById(R.id.recycler_piadine);

        }

        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

/*            if(layoutDettagli.getVisibility() == View.VISIBLE){
                layoutDettagli.setVisibility(View.GONE);
            }else{
                layoutDettagli.setVisibility(View.VISIBLE);
            }*/
        }
    }
    // allows clicks events to be caught
    public void setClickListener(OrdineListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface OrdineListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
