package com.example.sammy1997.bitswallet.utils;


import android.util.Log;

import com.example.sammy1997.bitswallet.models.Transaction;

import java.util.Comparator;

public class TransactionComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction o1, Transaction o2) {
        Log.e("Date : ",o1.getDate().toString());
        return o2.getDate().compareTo(o1.getDate());
    }

}
