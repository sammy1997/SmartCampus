package com.example.sammy1997.bitswallet.listners;

import com.example.sammy1997.bitswallet.models.Transaction;

import java.util.List;

/**
 * Created by sammy on 10/2/18.
 */

public interface OnDataLoadedListner{
    void onDataLoaded(List<Transaction> transactions, boolean listEmpty);
}
