package com.example.sammy1997.bitswallet.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.models.BillItem;

import java.util.List;

/**
 * Created by sammy on 10/2/18.
 */

public class BilledItemsAdapter extends RecyclerView.Adapter<BilledItemsAdapter.ViewHolder>{

    List<BillItem> values;
    Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemName;
        TextView txtCost;
        TextView status;
        public ViewHolder(View v) {
            super(v);
            txtItemName =  v.findViewById(R.id.item_name);
            txtCost =  v.findViewById(R.id.cost);
            status = v.findViewById(R.id.status);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.receipt_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public BilledItemsAdapter(List<BillItem> data, Activity activity) {
        this.values=data;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position).getItemName();
        holder.txtItemName.setText(name);
        Log.e("Bind", name);
        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtCost.setText(values.get(position).getCost());
        holder.txtItemName.setTypeface(montReg);
        holder.txtCost.setTypeface(montLight);
    }


}
