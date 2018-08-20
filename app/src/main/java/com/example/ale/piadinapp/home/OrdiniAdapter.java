package com.example.ale.piadinapp.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Ordine;
import com.example.ale.piadinapp.classi.Piadina;

import java.lang.ref.WeakReference;
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

        holder.recyclerViewPiadine.setHasFixedSize(true);
        holder.recyclerViewPiadine.setLayoutManager(new LinearLayoutManager(mContext));

        ArrayList<Piadina> piadineOrdine = ordine.getCartItems();
        PiadineOrdineAdapter adapterPiadine = new PiadineOrdineAdapter(mContext, piadineOrdine);
        holder.recyclerViewPiadine.setAdapter(adapterPiadine);

        holder.buttonDettagliOrdine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.recyclerViewPiadine.getVisibility() == View.VISIBLE){
                    holder.recyclerViewPiadine.animate().alpha(0.0f);
                    holder.recyclerViewPiadine.setVisibility(View.GONE);
                }else{
                    holder.recyclerViewPiadine.setVisibility(View.VISIBLE);
                    holder.recyclerViewPiadine.animate().alpha(1.0f);
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
        Button buttonRiOrdina;
        Button buttonDettagliOrdine;
        RecyclerView recyclerViewPiadine;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dataOrdine = itemView.findViewById(R.id.data_ordine);
            buttonDettagliOrdine = itemView.findViewById(R.id.button_dettagli_ordine);
            buttonDettagliOrdine.setOnClickListener(this);
            buttonRiOrdina = itemView.findViewById(R.id.button_ri_ordina);
            layoutDettagli = itemView.findViewById(R.id.layout_dettagli);

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
