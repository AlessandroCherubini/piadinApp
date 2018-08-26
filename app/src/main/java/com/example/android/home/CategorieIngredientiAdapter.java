package com.example.android.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.activity.CustomizePiadinaActivity;
import com.example.android.adapters.AddIngredientAdapter;
import com.example.android.classi.Ingrediente;
import com.example.android.fragments.TabCreaPiadina;
import com.example.android.utility.DBHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.activity.CustomizePiadinaActivity.totaleIngredienti;
import static com.example.android.activity.CustomizePiadinaActivity.totalePiadina;

public class CategorieIngredientiAdapter extends RecyclerView.Adapter<CategorieIngredientiAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context mContext;;
    View ingredientiView;
    AddIngredientAdapter addIngredientiAdapter;
    RecyclerView recyclerViewIngredienti;
    TextView prezzoPiadina;
    TabCreaPiadina creaPiadina;

    double totaleCreaPiadina;
    double totaleIngredientiCreaPiadina;
    double totaleImpastoEFormatoCreaPiadina;

    private DBHelper helper;
    private boolean fromHome = false;

    // data is passed into the constructor
    public CategorieIngredientiAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public CategorieIngredientiAdapter(Context context, List<String> data, TabCreaPiadina creaPiadina) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.creaPiadina = creaPiadina;

        totaleCreaPiadina = creaPiadina.getTotalePiadina();

        fromHome = true;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_categorie, parent, false);
        mContext = parent.getContext();
        helper = new DBHelper(mContext);
        ingredientiView = parent;
        //mContext
        recyclerViewIngredienti = parent.findViewById(R.id.ingredients);

        return new ViewHolder(view);

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String categoria = mData.get(position);
        holder.myTextView.setText(categoria);

        holder.recyclerViewAddIngredienti.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(holder.recyclerViewAddIngredienti.getVisibility()){
                    case View.GONE:
                        holder.recyclerViewAddIngredienti.setVisibility(View.VISIBLE);
                        holder.recyclerViewAddIngredienti.animate().alpha(1.0f);
                        holder.showButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        break;
                    case View.VISIBLE:
                        holder.recyclerViewAddIngredienti.setVisibility(View.GONE);
                        holder.recyclerViewAddIngredienti.animate().alpha(0.0f);
                        holder.showButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }

            }
        });

        holder.showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(holder.recyclerViewAddIngredienti.getVisibility()){
                    case View.GONE:
                        holder.recyclerViewAddIngredienti.setVisibility(View.VISIBLE);
                        holder.recyclerViewAddIngredienti.animate().alpha(1.0f);
                        holder.showButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        break;
                    case View.VISIBLE:
                        holder.recyclerViewAddIngredienti.setVisibility(View.GONE);
                        holder.recyclerViewAddIngredienti.animate().alpha(0.0f);
                        holder.showButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        // Prendo l'adapter e la lista degli Ingredienti correnti della piadina.
        final IngredientsAdapter adapter = (IngredientsAdapter) holder.recyclerViewIngredienti.getAdapter();
        final List<Ingrediente> listaIngredientiCorrenti = adapter.getArrayList();

        // Recycler View per la lista di ingredienti per ogni Categoria.
        final ArrayList<Ingrediente> ingredienti = helper.getIngredientiByCategoria(categoria);
        holder.recyclerViewAddIngredienti.setLayoutManager(new LinearLayoutManager(mContext));

        addIngredientiAdapter = new AddIngredientAdapter(mContext, ingredienti);
        addIngredientiAdapter.setClickListener(new AddIngredientAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                switch(view.getId()){
                    case R.id.allergeniAddButton:
                        // Click sull'allergeno dell'ingrediente da aggiungere.
                        DialogInterface.OnClickListener allergeniClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEUTRAL:

                                }
                            }
                        };

                        AlertDialog.Builder builderAllergenti = new AlertDialog.Builder(mContext);
                        builderAllergenti.setTitle("Allergeni relativi a " + ingredienti.get(position).getName());
                        builderAllergenti.setIcon(R.drawable.ic_info_black_24dp);
                        builderAllergenti.setMessage(ingredienti.get(position).getListaAllergeni()).
                                setNeutralButton("Ok", allergeniClickListener).show();
                        break;

                    case R.id.addButton:
                        // Click sul pulsante per aggiungere l'ingrediente alla piadina
                        Double prezzoIngrediente = ingredienti.get(position).getPrice();
                        listaIngredientiCorrenti.add(ingredienti.get(position));
                        adapter.notifyDataSetChanged();

                        // Fragment Crea la Tua Piadina
                        if(fromHome){
                            totaleIngredienti = creaPiadina.getTotaleIngredienti();
                            totaleImpastoEFormatoCreaPiadina = creaPiadina.getTotaleImpastoEFormato();
                            totaleCreaPiadina  = totaleImpastoEFormatoCreaPiadina + totaleIngredienti + prezzoIngrediente;

                            creaPiadina.setTotaleIngredienti(totaleIngredienti + prezzoIngrediente);
                            creaPiadina.setTotalePiadina(totaleCreaPiadina);

                            holder.layoutFragmentCreaPiadina.findViewById(R.id.greca_ingredienti_crea_piadina).setVisibility(View.VISIBLE);
                            holder.layoutFragmentCreaPiadina.findViewById(R.id.ingredienti_crea_piadina).setVisibility(View.VISIBLE);
                            holder.layoutFragmentCreaPiadina.findViewById(R.id.scroll_ingredienti_crea_piadina).setVisibility(View.VISIBLE);
                        }else{
                            // Customize Piadina Activity
                            totalePiadina = totalePiadina + prezzoIngrediente;
                            totaleIngredienti = ((CustomizePiadinaActivity) mContext).getTotaleIngredienti();
                            totaleIngredienti = totaleIngredienti + prezzoIngrediente;

                            ((CustomizePiadinaActivity) mContext).setTotaleIngredienti(totaleIngredienti);
                            ((CustomizePiadinaActivity) mContext).setTotalePiadina(totalePiadina);
                        }

                        Toast.makeText(mContext, "Ingrediente aggiunto", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        holder.recyclerViewAddIngredienti.setAdapter(addIngredientiAdapter);

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
        RecyclerView recyclerViewAddIngredienti;
        RecyclerView recyclerViewIngredienti;
        RelativeLayout layoutFragmentCreaPiadina;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.categoria);

            showButton = itemView.findViewById(R.id.dropDownButton);

            recyclerViewAddIngredienti = itemView.findViewById(R.id.recycler_ingredienti);
            if(fromHome){
                recyclerViewIngredienti = ((Activity) mContext).findViewById(R.id.ingredients_crea_piadina);
                prezzoPiadina = ((Activity) mContext).findViewById(R.id.prezzoTotalePiadina_crea_piadina);
                layoutFragmentCreaPiadina = ((Activity) mContext).findViewById(R.id.layout_fragment_crea_piadina);

            }else{
                recyclerViewIngredienti = ((Activity) mContext).findViewById(R.id.ingredients);
                prezzoPiadina = ((Activity) mContext).findViewById(R.id.prezzoTotalePiadina);
            }

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
