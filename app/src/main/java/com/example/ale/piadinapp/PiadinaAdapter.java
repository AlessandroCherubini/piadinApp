package com.example.ale.piadinapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Belal on 10/18/2017.
 */


public class PiadinaAdapter extends RecyclerView.Adapter<PiadinaAdapter.PiadinaViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Piadina> piadinaList;
    //Listener for buttons
    private final ClickListener listener;

    //getting the context and product list with constructor
    public PiadinaAdapter(Context mCtx, List<Piadina> piadinaList,ClickListener listener ) {
        this.mCtx = mCtx;
        this.listener = listener;
        this.piadinaList = piadinaList;
    }

    @Override
    public PiadinaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_piadina, null);
        return new PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PiadinaViewHolder holder, int position) {
        //getting the product of the specified position
        Piadina piadina = piadinaList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(piadina.getNome());
        String formattedString = piadina.ingredienti.toString()
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();
        holder.textViewIngredients.setText(formattedString);
        //holder.textViewRating.setText(String.valueOf(piadina.getRating()));
        holder.textViewPrice.setText(String.valueOf(piadina.getPrice()));

    }


    @Override
    public int getItemCount() {
        return piadinaList.size();
    }


    class PiadinaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewIngredients, textViewRating, textViewPrice;
        ImageButton addButton;
        private WeakReference<ClickListener> listenerRef;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewIngredients = itemView.findViewById(R.id.textViewIngredients);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            addButton = itemView.findViewById(R.id.addButton);
            listenerRef = new WeakReference<>(listener);

            //itemView.setOnClickListener(this);
            addButton.setOnClickListener(this);
        }

        public void onClick(View v) {

//            if (v.getId() == addButton.getId()) {
//                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


}
