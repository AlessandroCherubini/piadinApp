package com.example.ale.piadinapp.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.Piadina;

import java.lang.ref.WeakReference;
import java.util.List;

public class LeMiePiadineAdapter extends RecyclerView.Adapter<LeMiePiadineAdapter.PiadinaViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Piadina> piadinaList;
    //Listener for buttons
    private final ClickListener listener;

    //getting the context and product list with constructor
    public LeMiePiadineAdapter(Context mCtx, List<Piadina> piadinaList, ClickListener listener ) {
        this.mCtx = mCtx;
        this.listener = listener;
        this.piadinaList = piadinaList;
    }

    @Override
    public PiadinaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_la_mia_piadina, null);
        return new PiadinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PiadinaViewHolder holder, int position) {
        //getting the product of the specified position
        Piadina piadina = piadinaList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(piadina.getNome());
        holder.textViewIngredients.setText(piadina.printIngredienti());
        //holder.textViewRating.setText(String.valueOf(piadina.getRating()));
        holder.textViewPrezzo.setText(String.valueOf(piadina.getPrice()));
        //piadina.printDettagliIngredienti();

    }


    @Override
    public int getItemCount() {
        return piadinaList.size();
    }


    class PiadinaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewIngredients, textViewRating, textViewPrezzo;
        Button rateButton;
        private WeakReference<ClickListener> listenerRef;

        public PiadinaViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.nome_la_mia_piadina);
            textViewIngredients = itemView.findViewById(R.id.descrizione_la_mia_piadina);
            textViewPrezzo = itemView.findViewById(R.id.prezzo_la_mia_piadina);
            rateButton = itemView.findViewById(R.id.button_rate_la_mia_piadina);
            listenerRef = new WeakReference<>(listener);


            rateButton.setOnClickListener(this);
        }

        public void onClick(View v) {
            listenerRef.get().onPositionClicked(v, getAdapterPosition());
        }
    }


}
