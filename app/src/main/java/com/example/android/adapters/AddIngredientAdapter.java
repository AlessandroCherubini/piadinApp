package com.example.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.classi.Ingrediente;

import java.math.BigDecimal;
import java.util.List;

public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder>{
    private List<Ingrediente> mData;
    private LayoutInflater mInflater;
    private AddIngredientAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public AddIngredientAdapter(Context context, List<Ingrediente> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public AddIngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_add_ingredienti, parent, false);
        return new AddIngredientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddIngredientAdapter.ViewHolder holder, int position) {
        Ingrediente ingrediente = mData.get(position);

        holder.myTextView.setText(ingrediente.getName());
        double prezzoIngrediente = ingrediente.getPrice();

        BigDecimal totale = new BigDecimal(prezzoIngrediente);
        totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        holder.textPrice.setText(totale.toPlainString().replace(".", ",") + " €");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {

        return mData.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton addButton;
        ImageButton allergeniButton;
        TextView textPrice;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.add_ingrediente);
            allergeniButton = itemView.findViewById(R.id.allergeniAddButton);
            addButton = itemView.findViewById(R.id.addButton);
            allergeniButton.setOnClickListener(this);
            addButton.setOnClickListener(this);
            textPrice = itemView.findViewById(R.id.prezzo_add_ingrediente);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

        }

    }
    public void removeItem(int id){
        mData.remove(id);
        notifyItemRemoved(id);
        notifyItemRangeChanged(id,mData.size());
    }

    // convenience method for getting data at click position
    Ingrediente getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
/*    public void setClickListener(IngredientsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }*/

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
