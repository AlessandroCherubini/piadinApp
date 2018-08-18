package com.example.ale.piadinapp.home;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.FasciaOraria;

import java.util.ArrayList;

public class FasceOrarioAdapter extends RecyclerView.Adapter<FasceOrarioAdapter.ViewHolder> {

    private ArrayList<FasciaOraria> mData;
    private LayoutInflater mInflater;
    private fasciaOrarioListener mClickListener;
    private Context mContext;
    int checkPosition = 99;
    boolean isChecked = false;
    ViewHolder checkedView;

    public FasceOrarioAdapter(Context context, ArrayList<FasciaOraria> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_fascia_orario, parent, false);
        mContext = parent.getContext();
        return new FasceOrarioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FasciaOraria fasciaOraria = mData.get(position);

        holder.inizioFascia.setText(fasciaOraria.getInizioFascia());
        holder.fineFascia.setText(fasciaOraria.getFineFascia());

        if(fasciaOraria.isOccupata()){
            holder.buttonColoreFascia.setVisibility(View.INVISIBLE);
            holder.buttonSelectedFascia.setBackgroundResource(R.drawable.ic_do_not_disturb_black_24dp);
            holder.buttonSelectedFascia.setVisibility(View.VISIBLE);
            holder.singolaFascia.setBackgroundColor(Color.parseColor("#dddbdd"));
        }else{
            switch(fasciaOraria.getColoreBadge()){
                case 1:
                    holder.buttonColoreFascia.setBackgroundResource(R.drawable.ic_brightness_1_green_24dp);
                    break;
                case 2:
                    holder.buttonColoreFascia.setBackgroundResource(R.drawable.ic_brightness_1_yellow_24dp);
                    break;
                case 3:
                    holder.buttonColoreFascia.setBackgroundResource(R.drawable.ic_brightness_1_red_24dp);
                    break;
                default:
                    break;
            }
            checkedView = holder;

            holder.singolaFascia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(position != checkPosition){
                        checkPosition = position;

                        if(holder.buttonSelectedFascia.getVisibility() == View.VISIBLE){
                            holder.buttonSelectedFascia.setVisibility(View.GONE);
                        }else{
                            holder.buttonSelectedFascia.setVisibility(View.VISIBLE);
                        }
                        // todo: BUG!!!
                        // todo: se clicco sulla stessa fascia dopo averlo fatto sparire, non si visualizza più
                        // todo: perché checkedView = holder e lo setta visibile per poi farlo sparire.
                        checkedView.buttonSelectedFascia.setVisibility(View.GONE);
                        checkedView = holder;
                    }else{
                        holder.buttonSelectedFascia.setVisibility(View.GONE);
                        checkPosition = 99;
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public FasciaOraria getFasciaSelezionata(){
        FasciaOraria fasciaOraria;
        try{
             fasciaOraria = mData.get(checkPosition);
        }catch(IndexOutOfBoundsException e){
            fasciaOraria = null;
        }

        return fasciaOraria;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView inizioFascia;
        TextView fineFascia;
        ImageButton buttonColoreFascia;
        ImageButton buttonSelectedFascia;
        RelativeLayout singolaFascia;

        public ViewHolder(View itemView) {
            super(itemView);

            inizioFascia = itemView.findViewById(R.id.inizio_fascia);
            fineFascia = itemView.findViewById(R.id.fine_fascia);

            buttonSelectedFascia = itemView.findViewById(R.id.badge_selected);
            buttonColoreFascia = itemView.findViewById(R.id.button_fascia_color);
            singolaFascia = itemView.findViewById(R.id.singola_fascia);

        }

        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());

        }
    }
    // allows clicks events to be caught
    public void setClickListener(fasciaOrarioListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface fasciaOrarioListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
