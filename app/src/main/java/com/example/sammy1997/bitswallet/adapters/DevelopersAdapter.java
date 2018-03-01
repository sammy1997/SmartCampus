package com.example.sammy1997.bitswallet.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.listners.OnRemoveFromCartListener;
import com.example.sammy1997.bitswallet.models.CartItem;
import com.example.sammy1997.bitswallet.models.Developer;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class DevelopersAdapter extends RecyclerView.Adapter<DevelopersAdapter.ViewHolder> {

    List<Developer> developers;
    Activity activity;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView developerName;
        public TextView department;
        public TextView role;
        public ImageView phone;
        public ImageView mail;
        public CircleImageView image;

        public ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.profile_image);
            developerName = v.findViewById(R.id.developer_name);
            department = v.findViewById(R.id.developer_dept);
            role = v.findViewById(R.id.developer_role);
            phone = v.findViewById(R.id.developer_phn);
            mail = v.findViewById(R.id.developer_mail);
        }
    }

    @Override
    public DevelopersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.developer_item, parent, false);
        DevelopersAdapter.ViewHolder vh = new DevelopersAdapter.ViewHolder(v);
        return vh;
    }

    public DevelopersAdapter(List<Developer> data, Activity activity) {
        this.developers=data;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(DevelopersAdapter.ViewHolder holder, final int position) {
        final Developer developer = developers.get(position);
        holder.developerName.setText( developer.getName());
        holder.department.setText(developer.getDept());
        holder.role.setText(developer.getRole());
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + developer.getPhoneNumber().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                activity.startActivity(intent);
            }
        });
        holder.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ developer.getMailId()});
                email.putExtra(Intent.EXTRA_SUBJECT, "Query");
                email.setType("message/rfc822");
                activity.startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        StorageReference reference = FirebaseStorage.getInstance().getReference().child(developer.getImageUrl());
        Glide.with(activity).using(new FirebaseImageLoader())
                .load(reference).into(holder.image);
    }

        @Override
    public int getItemCount() {
        return developers.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


}
