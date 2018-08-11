package com.example.ale.piadinapp.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.ale.piadinapp.R;
import com.example.ale.piadinapp.classi.CartItem;
import com.example.ale.piadinapp.classi.Piadina;

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

        TextView tvNome, formato, tvImpasto, tvIngredienti, tvPrezzo;
        ImageButton remButton;
        private WeakReference<ClickListener> listenerRef;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.tv_nome);
            formato = itemView.findViewById(R.id.formato);
            tvImpasto = itemView.findViewById(R.id.tv_impasto);
            tvIngredienti = itemView.findViewById(R.id.tv_ingredienti);
            tvPrezzo = itemView.findViewById(R.id.tv_prezzo);
            remButton = itemView.findViewById(R.id.remButton);
            listenerRef = new WeakReference<>(listener);

            itemView.setOnClickListener(this);
            remButton.setOnClickListener(this);
        }

        public void onClick(View v) {

            if (v.getId() == remButton.getId()){

                listenerRef.get().onPositionClicked(getAdapterPosition());

            }
            else if (v.getId()== itemView.getId()){

                listenerRef.get().onCartItemClicked(getAdapterPosition());

            }
//            if (v.getId() == addButton.getId()) {
//                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            }


        }
    }



}


