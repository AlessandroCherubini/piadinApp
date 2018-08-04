package com.example.ale.piadinapp.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Ingrediente;
import com.example.ale.utility.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class CategorieIngredientiAdapter extends RecyclerView.Adapter<CategorieIngredientiAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;
    AddIngredientAdapter addIngredientiAdapter;
    private DBHelper helper;

    // data is passed into the constructor
    CategorieIngredientiAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_categorie, parent, false);
        mContext = parent.getContext();
        helper = new DBHelper(mContext);
        return new ViewHolder(view);

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String categoria = mData.get(position);
        holder.myTextView.setText(categoria);

        ArrayList<Ingrediente> ingredienti = helper.getIngredientiByCategoria(categoria);
        holder.reclycleIngredienti.setLayoutManager(new LinearLayoutManager(mContext));

        addIngredientiAdapter = new AddIngredientAdapter(mContext, ingredienti);
/*        addIngredientiAdapter.setClickListener(new AddIngredientAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });*/
        holder.reclycleIngredienti.setAdapter(addIngredientiAdapter);


    }

    // total number of rows
    @Override
    public int getItemCount() {

        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton showButton;
        RecyclerView reclycleIngredienti;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.categoria);
            showButton = itemView.findViewById(R.id.dropDownButton);
            // tutto l'elemento dell'adapter attiva il listener; se metto il pulsante show crasha!
            itemView.setOnClickListener(this);

            reclycleIngredienti = itemView.findViewById(R.id.recycler_ingredienti);

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
    String getItem(int id) {

        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }
}
