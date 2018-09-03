package com.example.android.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.activity.CustomizePiadinaActivity;
import com.example.android.classi.Ingrediente;
import com.example.android.fragments.TabCreaPiadina;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<Ingrediente> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private View ingredientiView;
    private double totalePiadina;
    private double totaleIngredienti;
    private double totaleImpastoEFormato;

    Context mContext;
    private boolean fromHome = false;
    TabCreaPiadina fragmentCreaPiadina;


    // data is passed into the constructor
    public IngredientsAdapter(Context context, List<Ingrediente> data) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        this.mData = data;
        totaleImpastoEFormato = ((CustomizePiadinaActivity) context).getTotaleImpastoEFormato();
        totalePiadina = ((CustomizePiadinaActivity) context).getTotalePiadina();
    }
    //
    public IngredientsAdapter(Context context, List<Ingrediente> data, TabCreaPiadina fragmentCreaPiadina) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
        this.fragmentCreaPiadina = fragmentCreaPiadina;
        totalePiadina = fragmentCreaPiadina.getTotalePiadina();
        totaleImpastoEFormato = fragmentCreaPiadina.getTotaleImpastoEFormato();

        fromHome = true;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_ingrediente, parent, false);
        ingredientiView = parent;

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Ingrediente ingrediente = mData.get(position);
        holder.myTextView.setText(ingrediente.getName());

        double prezzoIngrediente = ingrediente.getPrice();
        BigDecimal totale = new BigDecimal(prezzoIngrediente);
        totale = totale.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        holder.prezzoIngrediente.setText(totale.toPlainString().replace(".", ",") + " €");

        final IngredientsAdapter adapter = (IngredientsAdapter) holder.recyclerIngredienti.getAdapter();

        holder.allergeniButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener allergeniClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEUTRAL:

                        }
                    }
                };

                AlertDialog.Builder builderAllergenti = new AlertDialog.Builder(mContext);
                builderAllergenti.setTitle("Allergeni relativi a " + adapter.getItem(position).getName());
                builderAllergenti.setIcon(R.drawable.ic_info_black_24dp);
                builderAllergenti.setMessage(adapter.getItem(position).getListaAllergeni()).
                        setNeutralButton("Ok", allergeniClickListener).show();
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                Double prezzoIngrediente = adapter.getItem(position).getPrice();
                                totaleIngredienti = adapter.getPrezzoIngredienti(mData);

                                if(fromHome){
                                    totaleImpastoEFormato = fragmentCreaPiadina.getTotaleImpastoEFormato();
                                    totalePiadina = totaleImpastoEFormato + totaleIngredienti - prezzoIngrediente;

                                    fragmentCreaPiadina.setTotaleIngredienti(totaleIngredienti - prezzoIngrediente);
                                    fragmentCreaPiadina.setTotalePiadina(totalePiadina);
                                }else{
                                    totalePiadina = ((CustomizePiadinaActivity) mContext).getTotalePiadina();
                                    totalePiadina = totalePiadina - prezzoIngrediente;
                                    ((CustomizePiadinaActivity) mContext).setTotalePiadina(totalePiadina);
                                }

                                adapter.removeItem(position);
                                notifyItemChanged(getItemCount() - 1);
                                holder.recyclerIngredienti.swapAdapter(adapter,true);

                                Toast.makeText(mContext, "Ingrediente rimosso", Toast.LENGTH_SHORT).show();

                                if (adapter.getItemCount() == 0) {
                                    Toast.makeText(mContext, "La piadina è vuota!", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                // Non si fa niente!

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Elimina");
                builder.setMessage("Vuoi rimuovere " + adapter.getItem(position).getName() + " ?").setPositiveButton("Sì, Elimina", dialogClickListener)
                        .setNegativeButton("Annulla", dialogClickListener).show();


            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {

        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton removeButton;
        ImageButton allergeniButton;
        TextView prezzoIngrediente;
        TextView prezzoPiadina;
        RecyclerView recyclerIngredienti;

        public ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.ingredient);
            allergeniButton = itemView.findViewById(R.id.allergeniButton);
            removeButton = itemView.findViewById(R.id.removeButton);
            prezzoIngrediente = itemView.findViewById(R.id.textViewPrice);

            if(fromHome){
                recyclerIngredienti = ingredientiView.findViewById(R.id.ingredients_crea_piadina);
                prezzoPiadina = itemView.findViewById(R.id.prezzoTotalePiadina_crea_piadina);

            }else{
                recyclerIngredienti = ingredientiView.findViewById(R.id.ingredients);
                prezzoPiadina = ((Activity) mContext).findViewById(R.id.prezzoTotalePiadina);
            }

        }

        @Override
        public void onClick(View view) {
            int position = view.getLayoutDirection();
            mClickListener.onItemClick(view, position);

        }

    }
    public void removeItem(int id){
        mData.remove(id);
        notifyItemRemoved(id);
        notifyItemRangeChanged(id,mData.size());
    }

    // convenience method for getting data at click position
    public Ingrediente getItem(int id) {
        return mData.get(id);
    }

    public List<Ingrediente> getArrayList(){
        return mData;
    }

    public ArrayList<Ingrediente> getIngredientiArrayList(){
        ArrayList<Ingrediente> ingredienti = new ArrayList<>();

        for(Ingrediente ingrediente: mData){
            ingredienti.add(ingrediente);
        }

        return ingredienti;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public double getPrezzoIngredienti(List<Ingrediente> ingredienti){
        double totale = 0;
        for(Ingrediente ingrediente:ingredienti){
            totale = totale + ingrediente.getPrice();
        }
        return totale;
    }
}
