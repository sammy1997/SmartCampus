package com.example.sammy1997.bitswallet.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.listners.OnAddToCartButtonListener;
import com.example.sammy1997.bitswallet.models.Item;

import java.util.List;

/**
 * Created by sammy on 9/2/18.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{

    List<Item> values;
    Activity activity;
    OnAddToCartButtonListener addToCartButtonListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItemName;
        public TextView txtCost;
        public ImageView addToCart;

        public ViewHolder(View v) {
            super(v);
            txtItemName =  v.findViewById(R.id.item_name);
            txtCost =  v.findViewById(R.id.cost);
            addToCart = v.findViewById(R.id.add_to_cart_button);
        }
    }

    public ItemsAdapter(List<Item> data, Activity activity, OnAddToCartButtonListener addToCartButtonListener) {
        values=data;
        this.activity = activity;
        this.addToCartButtonListener = addToCartButtonListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.add_to_cart_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position).getItemName();
        holder.txtItemName.setText(name);
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartButtonListener.onAddToCartClicked(values.get(position));
            }
        });

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtCost.setText("INR " + values.get(position).getCost());
        holder.txtItemName.setTypeface(montReg);
        holder.txtCost.setTypeface(montLight);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
