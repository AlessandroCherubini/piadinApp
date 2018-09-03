package com.example.android.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carteasy.v1.lib.Carteasy;
import com.example.android.R;
import com.example.android.activity.MyOrderActivity;
import com.example.android.classi.Ordine;
import com.example.android.classi.Piadina;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;


public class OrdiniAdapter extends RecyclerView.Adapter<OrdiniAdapter.ViewHolder>{

    private ArrayList<Ordine> mData;
    private LayoutInflater mInflater;
    private OrdineListener mClickListener;
    private Context mContext;
    RelativeLayout layoutDettagli;

    private Carteasy cs = new Carteasy();
    Map<Integer, Map> data;
    String identificatore;

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
        holder.dataOrdine.setText(ordine.getDataOrdine());
        holder.timestampOrdine.setText(ordine.getTimestampOrdine());
        holder.fasciaOrdine.setText(ordine.getFasciaOrdine());
        int colore = ordine.getColoreOrdine();


        double totaleOrdine = ordine.getPrezzoOrdine();

        BigDecimal totale = new BigDecimal(totaleOrdine);
        totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        holder.totaleOrdine.setText(totale.toPlainString().replace(".", ","));

        switch(colore){
            case 1:
                holder.coloreOrdine.setBackgroundResource(R.drawable.ic_looks_one_black_24dp);
                break;
            case 2:
                holder.coloreOrdine.setBackgroundResource(R.drawable.ic_looks_two_black_24dp);
                break;
            case 3:
                holder.coloreOrdine.setBackgroundResource(R.drawable.ic_looks_3_black_24dp);
                break;
            default:
                break;
        }

        holder.recyclerViewPiadine.setHasFixedSize(true);
        holder.recyclerViewPiadine.setLayoutManager(new LinearLayoutManager(mContext));

        final ArrayList<Piadina> piadineOrdine = ordine.getCartItems();
        PiadineOrdineAdapter adapterPiadine = new PiadineOrdineAdapter(mContext, piadineOrdine);
        holder.recyclerViewPiadine.setAdapter(adapterPiadine);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.layoutDettagliOrdine.getVisibility() == View.VISIBLE){
                    holder.layoutDettagliOrdine.setVisibility(View.GONE);
                    holder.layoutDettagliOrdine.animate().alpha(0.0f);
                    holder.buttonDropDownMenu.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }else{
                    holder.layoutDettagliOrdine.setVisibility(View.VISIBLE);
                    holder.layoutDettagliOrdine.animate().alpha(1.0f);
                    holder.buttonDropDownMenu.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }

            }
        });

        holder.buttonDropDownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.layoutDettagliOrdine.getVisibility() == View.VISIBLE){
                    holder.layoutDettagliOrdine.setVisibility(View.GONE);
                    holder.layoutDettagliOrdine.animate().alpha(0.0f);
                    holder.buttonDropDownMenu.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }else{
                    holder.layoutDettagliOrdine.setVisibility(View.VISIBLE);
                    holder.layoutDettagliOrdine.animate().alpha(1.0f);
                    holder.buttonDropDownMenu.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }

            }
        });


        holder.buttonRiOrdina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Vuoi aggiungere tutte le piadine dell'ordine al carrello?");

                builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for(Piadina piadina: piadineOrdine){
                            aggiungiAlCarrello(piadina);
                        }

                        if(piadineOrdine.size() == 1){
                            Toast.makeText(mContext, "Piadina aggiunta al carrello", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext, "Piadine aggiunte al carrello", Toast.LENGTH_SHORT).show();
                        }

                        ((MyOrderActivity) mContext).finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dataOrdine;
        TextView timestampOrdine;
        TextView totaleOrdine;
        TextView fasciaOrdine;
        ImageView coloreOrdine;
        Button buttonDropDownMenu;
        Button buttonRiOrdina;
        RecyclerView recyclerViewPiadine;
        RelativeLayout layoutDettagliOrdine;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            dataOrdine = itemView.findViewById(R.id.data_ordine_text_riepilogo);
            timestampOrdine = itemView.findViewById(R.id.data_ordine);
            totaleOrdine = itemView.findViewById(R.id.prezzo_piadine_ordine);
            fasciaOrdine = itemView.findViewById(R.id.fascia_ordine);
            buttonDropDownMenu = itemView.findViewById(R.id.button_dropdown_ordini);
            coloreOrdine = itemView.findViewById(R.id.colore_fascia);
            buttonRiOrdina = itemView.findViewById(R.id.button_ri_ordina);
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

    }

}
