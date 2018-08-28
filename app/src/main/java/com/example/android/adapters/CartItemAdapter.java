package com.example.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.android.R;
import com.example.android.classi.CartItem;
import com.example.android.home.ClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ItemViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<CartItem> itemList;
    //Listener for buttons
    private final ClickListener listener;

    //getting the context and product list with constructor
    public CartItemAdapter(Context mCtx, List<CartItem>itemList,ClickListener listener ) {
        this.mCtx = mCtx;
        this.listener = listener;
        this.itemList=itemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_cart_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        //getting the product of the specified position
        CartItem item = itemList.get(position);

        //binding the data with the viewholder views
        holder.tvNome.setText(item.getNome());
        holder.formato.setText(item.getFormato());
        holder.tvImpasto.setText(item.getImpasto());
        holder.tvIngredienti.setText(item.printIngredienti());
        holder.tvPrezzo.setText(String.valueOf(item.getPrezzo()));
        holder.tvQuantita.setText(String.valueOf(item.getQuantita()));

    }

    public CartItem getItem(int id){

        return itemList.get(id);
    }

    public void removeItem(int id){
        itemList.remove(id);
        notifyItemRemoved(id);
        notifyItemRangeChanged(id,itemList.size());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNome, formato, tvImpasto, tvIngredienti, tvPrezzo, tvQuantita;
        ImageButton remButton;
        ImageButton editButton;
        private WeakReference<ClickListener> listenerRef;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.tv_nome);
            formato = itemView.findViewById(R.id.formato);
            tvImpasto = itemView.findViewById(R.id.tv_impasto);
            tvIngredienti = itemView.findViewById(R.id.tv_ingredienti);
            tvPrezzo = itemView.findViewById(R.id.tv_prezzo);
            tvQuantita = itemView.findViewById(R.id.tv_quantita);

            editButton = itemView.findViewById(R.id.editButton);
            remButton = itemView.findViewById(R.id.remButton);

            listenerRef = new WeakReference<>(listener);

            editButton.setOnClickListener(this);
            remButton.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v.getId() == remButton.getId()){

                listenerRef.get().onPositionClicked(v, getAdapterPosition());

            } else if (v.getId() == editButton.getId()){
                listenerRef.get().onPositionClicked(v, getAdapterPosition());
            }
        }
    }

    public void clear(){
        itemList.clear();
    }
}


