package com.example.sammy1997.bitswallet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.adapters.DevelopersAdapter;
import com.example.sammy1997.bitswallet.listners.AddMoneyButtonClickListener;
import com.example.sammy1997.bitswallet.listners.BackPressedListener;
import com.example.sammy1997.bitswallet.listners.CentralButtonRemoveListener;
import com.example.sammy1997.bitswallet.listners.CloseBottomSheetListener;
import com.example.sammy1997.bitswallet.listners.OnReceiptItemClickListener;
import com.example.sammy1997.bitswallet.models.Developer;
import com.example.sammy1997.bitswallet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DevelopersFragment extends Fragment {

    List<Developer> developers;
    CentralButtonRemoveListener removeListener;
    BackPressedListener backPressedListener;

    public DevelopersFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_developers, container, false);
        developers = new ArrayList<>();
        getDevelopers();
        if (removeListener!=null){
            removeListener.centralButtonRemoveListener(true);
        }

        RecyclerView recyclerView = v.findViewById(R.id.developers_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new DevelopersAdapter(developers,getActivity());
        recyclerView.setAdapter(adapter);
        ImageView back = v.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backPressedListener!=null){
                    backPressedListener.onBackButtonFragment();
                }
            }
        });
        return v;
    }

    void getDevelopers(){
        for (int i =0; i<9;i++){
            Developer developer = new Developer();
            developer.setImageUrl(Utils.imageUrl[i]);
            developer.setName(Utils.names[i]);
            developer.setDept(Utils.dept[i]);
            developer.setRole(Utils.role[i]);
            developer.setMailId(Utils.email[i]);
            developer.setPhoneNumber(Utils.phone[i]);
            developers.add(developer);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            removeListener = (CentralButtonRemoveListener) context;
            backPressedListener = (BackPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBackPressedListener");
        }
    }

    @Override
    public void onDetach() {
        removeListener =null;
        backPressedListener = null;
        super.onDetach();
    }
}
