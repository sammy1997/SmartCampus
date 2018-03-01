package com.example.sammy1997.bitswallet.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.adapters.ItemsAdapter;
import com.example.sammy1997.bitswallet.listners.BackPressedListener;
import com.example.sammy1997.bitswallet.listners.OnAddToCartButtonListener;
import com.example.sammy1997.bitswallet.listners.OnCartViewButtonListener;
import com.example.sammy1997.bitswallet.models.Item;
import com.example.sammy1997.bitswallet.models.Shop;
import com.example.sammy1997.bitswallet.utils.URLS;
import com.example.sammy1997.bitswallet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.sammy1997.bitswallet.utils.Utils.userObject;


public class ItemsViewFragment extends Fragment {

    List<Item> items;
    Shop shop;
    ImageView cartView;
    ProgressBar progressBar;
    TextView tv;
    ImageView refresh;
    TextView header;
    BackPressedListener backPressedListener;
    OnCartViewButtonListener cartViewListener;
    OnAddToCartButtonListener addToCartButtonListener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public ItemsViewFragment() {
    }

    public static ItemsViewFragment newInstance(Shop shop) {
        ItemsViewFragment fragment = new ItemsViewFragment();
        fragment.shop = shop;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemsViewLayout = inflater.inflate(R.layout.fragment_items_view, container, false);
        recyclerView = itemsViewLayout.findViewById(R.id.item_list);
        items = new ArrayList<>();
        progressBar = itemsViewLayout.findViewById(R.id.shop_loader);
        tv = itemsViewLayout.findViewById(R.id.textView11);
        refresh = itemsViewLayout.findViewById(R.id.refresh);
        header = itemsViewLayout.findViewById(R.id.textView);
        getItems();
        cartView = itemsViewLayout.findViewById(R.id.view_cart);
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = itemsViewLayout.findViewById(R.id.textView);
        header.setTypeface(montSemiBold);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ItemsAdapter(items,this.getActivity(),addToCartButtonListener);
        recyclerView.setAdapter(adapter);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems();
            }
        });

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartViewListener.onCartViewButtonListener();
            }
        });
        ImageView back = itemsViewLayout.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressedListener.onBackButtonFragment();
            }
        });
        return itemsViewLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            backPressedListener = (BackPressedListener)context;
            addToCartButtonListener = (OnAddToCartButtonListener) context;
            cartViewListener = (OnCartViewButtonListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement addMoneyListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        backPressedListener = null;
        cartViewListener = null;
        addToCartButtonListener = null;
    }
    void getItems(){
        try {
            final String shopID = shop.getId();

            JSONObject user = userObject();
            progressBar.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
            refresh.setVisibility(View.GONE);
            AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String token = null;
                            try {
                                token = response.getString("token");
                                Log.e("Token", token);
                                AndroidNetworking.post(URLS.get_products + shopID).addJSONObjectBody(Utils.walletSecret())
                                        .addHeaders("Authorization", "JWT " + token).build().getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        JSONArray products = null;
                                        try {
                                            products = response.getJSONArray("products");
                                            header.setText(response.getJSONObject("stalls").getString("name"));
                                            for (int i = 0; i < products.length(); i++) {
                                                JSONObject object = products.getJSONObject(i);
                                                Item item = new Item();
                                                item.setItemName(object.getString("name"));
                                                item.setId(object.getString("id"));
                                                item.setIs_available(object.getBoolean("is_available"));
                                                item.setCost(object.getString("price"));
                                                item.setColor(object.getString("colour"));
                                                item.setSize(object.getString("size"));
                                                if (item.isIs_available()) {
                                                    items.add(item);
                                                }
                                            }

                                            adapter = new ItemsAdapter(items, getActivity(), addToCartButtonListener);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.INVISIBLE);

                                        } catch (JSONException e) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            tv.setVisibility(View.VISIBLE);
                                            refresh.setVisibility(View.VISIBLE);
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        tv.setVisibility(View.VISIBLE);
                                        refresh.setVisibility(View.VISIBLE);
                                    }
                                });
                            } catch (JSONException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                tv.setVisibility(View.VISIBLE);
                                refresh.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            progressBar.setVisibility(View.INVISIBLE);
                            tv.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.VISIBLE);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
//            Toast.makeText(getContext(),"Press back again",Toast.LENGTH_SHORT).show();
        }
    }
}
