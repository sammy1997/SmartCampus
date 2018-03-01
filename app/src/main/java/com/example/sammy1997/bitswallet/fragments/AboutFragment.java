package com.example.sammy1997.bitswallet.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.listners.BackPressedListener;
import com.example.sammy1997.bitswallet.listners.CentralButtonRemoveListener;


public class AboutFragment extends Fragment {


    CentralButtonRemoveListener removeListener;
    BackPressedListener backPressedListener;

    public AboutFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ImageView back = v.findViewById(R.id.back_button);
        if (removeListener!=null){
            removeListener.centralButtonRemoveListener(true);
        }
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
