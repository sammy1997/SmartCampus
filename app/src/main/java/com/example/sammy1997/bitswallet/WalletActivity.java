package com.example.sammy1997.bitswallet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.sammy1997.bitswallet.activities.ShopActivity;
import com.example.sammy1997.bitswallet.activities.WebViewActivity;
import com.example.sammy1997.bitswallet.adapters.BilledItemsAdapter;
import com.example.sammy1997.bitswallet.fragments.WalletEmptyFragment;
import com.example.sammy1997.bitswallet.fragments.WalletHomeFragment;
import com.example.sammy1997.bitswallet.fragments.WalletLoadFragment;
import com.example.sammy1997.bitswallet.listners.AddMoneyButtonClickListener;
import com.example.sammy1997.bitswallet.listners.BackPressedListener;
import com.example.sammy1997.bitswallet.listners.CloseBottomSheetListener;
import com.example.sammy1997.bitswallet.listners.OnDataLoadedListner;
import com.example.sammy1997.bitswallet.listners.OnReceiptItemClickListener;
import com.example.sammy1997.bitswallet.listners.TransactionCompleteListener;
import com.example.sammy1997.bitswallet.models.BillItem;
import com.example.sammy1997.bitswallet.models.Sales;
import com.example.sammy1997.bitswallet.models.Stall;
import com.example.sammy1997.bitswallet.models.Transaction;
import com.example.sammy1997.bitswallet.services.NotificationService;
import com.example.sammy1997.bitswallet.utils.URLS;
import com.example.sammy1997.bitswallet.utils.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.example.sammy1997.bitswallet.utils.Utils.userObject;

public class WalletActivity extends FragmentActivity implements TransactionCompleteListener, CloseBottomSheetListener,
        OnDataLoadedListner, AddMoneyButtonClickListener,OnReceiptItemClickListener, BackPressedListener {

    boolean clicked;
    BottomSheetBehavior sheetBehavior;
    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montBold;
    Typeface montLight;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    DrawerLayout drawer;
    Typeface montReg;
    Typeface montMedItalic;
    String url;
    boolean flag_qr;
    boolean flag_payment;
    FirebaseDatabase database;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LayoutInflater inflater;
    FrameLayout bottomSheetContents;
    ImageButton qrScan;
    AlertDialog alert;
    FloatingActionButton floatingActionButton;
    BottomSheetBehavior.BottomSheetCallback callback;
    void setTypefaces(){
       montBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Bold.ttf");
       montLight = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Light.ttf");
       montReg = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.ttf");
       montSemiBoldItalic = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBoldItalic.ttf");
       montMedium = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Medium.ttf");
       montSemiBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBold.ttf");
       montMedItalic = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-MediumItalic.ttf");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("In onCreate","called");
        setContentView(R.layout.layout_main_home);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("default", MODE_PRIVATE);
        Utils.username = prefs.getString("username", "");
        Utils.password = prefs.getString("password", "");

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        context=this;
        activity=this;
        flag_payment =false;
        flag_qr = false;
        callback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                Log.e("New State", String.valueOf(flag_qr));
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    if(flag_qr){
                        if (ActivityCompat.checkSelfPermission(WalletActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(WalletActivity.this, new String[] {Manifest.permission.CAMERA}, 700);
                        }else{
                            Intent intent = new Intent(activity,QrScanActivity.class);
                            intent.putExtra("scan","transfer");
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    }else if (flag_payment){
                        Intent i = new Intent(activity, WebViewActivity.class);
                        i.putExtra("url", url);
                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    }
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        };

        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);
        qrScan = findViewById(R.id.qr_scanner);
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(WalletActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WalletActivity.this, new String[] {Manifest.permission.CAMERA}, 699);
                }else{
                    Intent intent = new Intent(activity,QrScanActivity.class);
                    intent.putExtra("scan","shop");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
            }
        });
        database = FirebaseDatabase.getInstance();
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        editor = preferences.edit();
        setTypefaces();
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior= BottomSheetBehavior.from(bottomSheet);

        sheetBehavior.setBottomSheetCallback(callback);
        floatingActionButton = findViewById(R.id.central);
        floatingActionButton.setImageResource(R.drawable.food_icon);
        inflater = LayoutInflater.from(context);
        bottomSheetContents = findViewById(R.id.bottom_sheet_content);

        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment,walletLoadFragment).commit();

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageButton payOrReceive = findViewById(R.id.pay_receive);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(activity,ShopActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        boolean transfer_scanned = preferences.getBoolean("transfer_scanned",false);

        if (transfer_scanned){
            String qrcode = preferences.getString("qr_code","");
            editor.putBoolean("shop_qr_scanned", false);
            editor.putBoolean("transfer_scanned", false);
            editor.apply();
            transferMoney(qrcode);
        }

        payOrReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    View pay_receive = inflater.inflate(R.layout.transfer_receive_layout,
                            bottomSheetContents, false);
                    bottomSheet.getLayoutParams().height = Utils.dpToPx(400);
                    bottomSheet.requestLayout();
                    final ProgressBar progress = pay_receive.findViewById(R.id.progressBar4);
                    TextView topText = pay_receive.findViewById(R.id.textView8);
                    Button receiveButton = pay_receive.findViewById(R.id.receive_money);
                    Button transferMoney = pay_receive.findViewById(R.id.transfer_money);
                    topText.setTypeface(montSemiBoldItalic);
                    receiveButton.setTypeface(montMedium);
                    transferMoney.setTypeface(montMedium);
                    bottomSheetContents.removeAllViews();
                    bottomSheetContents.addView(pay_receive);
                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    transferMoney.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadTransferOptions();
                        }
                    });

                    receiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progress.setVisibility(View.VISIBLE);
                            View receiveMoney = inflater.inflate(R.layout.profile_qr_code_layout, bottomSheetContents, false);
                            bottomSheet.getLayoutParams().height =  Utils.dpToPx(420);
                            bottomSheet.requestLayout();
                            bottomSheetContents.removeAllViews();
                            bottomSheetContents.addView(receiveMoney);


                            ImageView qrCode = findViewById(R.id.qr_code);
                            String walletUID = preferences.getString("walletID","xx");
                            QRCodeWriter writer = new QRCodeWriter();
                            try {
                                BitMatrix bitMatrix = writer.encode(walletUID, BarcodeFormat.QR_CODE, 512, 512);
                                int width = bitMatrix.getWidth();
                                int height = bitMatrix.getHeight();
                                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                    }
                                }
                                qrCode.setImageBitmap(bmp);
                                progress.setVisibility(View.INVISIBLE);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            TextView name = receiveMoney.findViewById(R.id.name);
                            TextView college = receiveMoney.findViewById(R.id.college);
                            TextView userId = receiveMoney.findViewById(R.id.id_user);
                            name.setTypeface(montBold);
                            name.setText(preferences.getString("name","N.A"));
                            college.setTypeface(montMedium);
                            college.setText(preferences.getString("college","N.A"));
                            userId.setTypeface(montMedium);
                            userId.setText("UID: " + preferences.getString("userid","N.A"));


                        }
                    });
            }
        });
    }

    void startTransferScan(){
        if (sheetBehavior.getState()== BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    void loadUIDLayout(){
        View loadUID = inflater.inflate(R.layout.enter_uid_layout, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();

        TextView moneyHeader = loadUID.findViewById(R.id.textView6);
        moneyHeader.setTypeface(montSemiBoldItalic);

        final EditText uid = loadUID.findViewById(R.id.uid_text);

        Button btn = loadUID.findViewById(R.id.enter_uid_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uidVal =uid.getText().toString();
                if (uidVal.equals("")){
                    Toast.makeText(activity,"Enter a correct UID", Toast.LENGTH_SHORT).show();
                }else {
                    transferMoney(uidVal);
                }

            }
        });

        uid.setTypeface(montBold);
        btn.setTypeface(montReg);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(loadUID);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==699){
            if (grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(activity,QrScanActivity.class);
                    intent.putExtra("scan","shop");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow permissions to continue", Toast.LENGTH_LONG).show();
                }
            }
        }
        if(requestCode==700){
            if (grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(activity,QrScanActivity.class);
                    intent.putExtra("scan","transfer");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow permissions to continue", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void loadTransferOptions(){
        View transferMoney = inflater.inflate(R.layout.transfer_options_layout, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();

        TextView moneyHeader = transferMoney.findViewById(R.id.textView8);
        moneyHeader.setTypeface(montSemiBoldItalic);

        Button scanQr = transferMoney.findViewById(R.id.via_qr_scan);
        Button uidBtn = transferMoney.findViewById(R.id.enter_uid);
        scanQr.setTypeface(montReg);
        uidBtn.setTypeface(montReg);

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag_qr = true;
                flag_payment =false;
                startTransferScan();
            }
        });

        uidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUIDLayout();
            }
        });
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(transferMoney);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


    }

    void transferMoney(final String walletUID){
        View transferMoney = inflater.inflate(R.layout.pay_money, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = transferMoney.findViewById(R.id.textView6);
        final EditText amount = transferMoney.findViewById(R.id.editText);
        Button continueBtn = transferMoney.findViewById(R.id.pay_money_button);
        TextView rupeesText = transferMoney.findViewById(R.id.textView7);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setTypeface(montBold);
        final ProgressBar progressBar = transferMoney.findViewById(R.id.progress);
        continueBtn.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(transferMoney);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String amountText = amount.getText().toString();
                if (amountText.equals("")){
                    Toast.makeText(activity,"Enter an amount", Toast.LENGTH_SHORT).show();
                }else{
                    forwardTransferRequest(walletUID,amountText,progressBar);
                }

            }
        });

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    void forwardTransferRequest(final String id, final String amount, ProgressBar progressBar){
        int balance = Integer.valueOf(preferences.getString("balance","0"));
        final int amountTotal = Integer.parseInt(amount);

        if (balance<amountTotal){
            showAlert(amountTotal-balance);
        }else {
            JSONObject user = userObject();
            AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString("token");
                                Log.e("Token",token);
                                JSONObject object = new JSONObject();
                                object.put("uid",id);
                                object.put("WALLET_TOKEN",Utils.wallet_secret);
                                object.put("amount",amount);
                                AndroidNetworking.post(URLS.transfer).addHeaders("Authorization","JWT "+ token).addJSONObjectBody(object).build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String status = response.getString("status");
                                                    if (status.equals("1")){

                                                        try {
                                                            WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
                                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).addToBackStack("Payment").commitAllowingStateLoss();
                                                            showPaymentSuccess(amountTotal);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else {
                                                        showPaymentFailure(amountTotal);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    showPaymentFailure(amountTotal);
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Log.e("Error : ", anError.toString());
                                                showPaymentFailure(amountTotal);
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showPaymentFailure(amountTotal);
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("Error : ", anError.toString());
                            showPaymentFailure(amountTotal);
                        }
                    });

        }

    }

    void showAlert(final int amount){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You are short by Rs."+amount+". Please add to continue.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        addMoney(amount);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onDataLoaded(List<Transaction> transactions, boolean listEmpty) {
        if (listEmpty){
            WalletEmptyFragment walletEmptyFragment = new WalletEmptyFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletEmptyFragment,
                    "EmptyFragment").commit();
        }else{
            List<Transaction> transactions1 = new ArrayList<>();
            WalletHomeFragment walletHomeFragment = WalletHomeFragment.newInstance(transactions1);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletHomeFragment,
                                "Loaded Fragment").commit();
        }
    }


    void showPaymentSuccess(int amount){
        View success = inflater.inflate(R.layout.payment_success_layout,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();
        TextView tv1 = success.findViewById(R.id.textView16);
        TextView tv2 = success.findViewById(R.id.textView17);
        tv2.setText("" + amount);
        TextView tv3 = success.findViewById(R.id.textView18);
        tv1.setTypeface(montMedItalic);
        tv3.setTypeface(montMedItalic);
        tv2.setTypeface(montBold);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(success);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void showPaymentFailure(int amount){
        View failed = inflater.inflate(R.layout.payment_failed_layout,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();
        TextView tv1 = failed.findViewById(R.id.textView16);
        TextView tv2 = failed.findViewById(R.id.textView17);
        tv2.setText("" + amount);
        TextView tv3 = failed.findViewById(R.id.textView18);
        tv1.setTypeface(montMedItalic);
        tv3.setTypeface(montMedItalic);
        tv2.setTypeface(montBold);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(failed);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void addMoney(int amountToAdd){
        View addMoney = inflater.inflate(R.layout.add_money,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = addMoney.findViewById(R.id.textView6);
        final EditText amount = addMoney.findViewById(R.id.editText);
        Button addMoneyButton = addMoney.findViewById(R.id.add_money_button);
        Button addMoneyBitsian = addMoney.findViewById(R.id.add_money_bitsian);

        TextView rupeesText = addMoney.findViewById(R.id.textView7);
        final ProgressBar progressBar = addMoney.findViewById(R.id.progressBar3);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setText("" + amountToAdd);
        amount.setTypeface(montBold);
        addMoneyButton.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(addMoney);


        addMoneyBitsian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addAmountRequest1(Integer.valueOf(amount.getText().toString()),progressBar);
                Log.e("Add Money",amount.getText().toString());
            }
        });

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addAmountRequest(Integer.valueOf(amount.getText().toString()),progressBar);
                Log.e("Add Money",amount.getText().toString());
            }
        });

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void addAmountRequest1(final int amount, final ProgressBar progressBar){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("amount",amount);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.addMoneyBitsian).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    try {
                                        if (response.getInt("status") == 1) {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new WalletLoadFragment()).commit();
                                            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                            }
                                        }else {

                                            Toast.makeText(WalletActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        showPaymentFailure(amount);
                    }
                });
    }


    void addAmountRequest(final int amount, final ProgressBar progressBar){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("amount",amount);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.add_amount).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(final JSONObject response) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    try {
                                        Log.e("Add Response", response.getString("url"));

                                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }

                                        url = response.getString("url");
                                        flag_payment =true;
                                        flag_qr = false;

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        showPaymentFailure(amount);
                    }
                });
    }

    @Override
    public void onAddMoneyButtonClicked() {
        addMoney(100);
    }


    @Override
    public void onReceiptItemClicked(final Transaction transaction) {
        final LinearLayout layout = findViewById(R.id.overlayCard);
        final Button cancelOrder = findViewById(R.id.cancel_order);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        final Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        TextView heading = findViewById(R.id.heading);
        final TextView name = findViewById(R.id.name);
        TextView date = findViewById(R.id.date);
        TextView total = findViewById(R.id.grand_total);

        heading.setTypeface(montMedium);
        name.setTypeface(montMedItalic);
        date.setTypeface(montMedItalic);
        total.setTypeface(montMedItalic);

        date.setText(transaction.getCreated_at());
        heading.setText("Transaction ID : " + transaction.getId());
        total.setText("INR " + transaction.getValue());

cancelOrder.setText("GENERATE OTP");
cancelOrder.setBackgroundResource(R.drawable.active_btn);


        final String transactionType = transaction.getT_type();
        String nameText;
        if (transactionType.equals("buy")){
            heading.setText("Order ID : " + transaction.getStallgroup().getOrder_id());
            nameText = transaction.getStallgroup().getStallname();
            cancelOrder.setVisibility(View.VISIBLE);
            Stall stall=transaction.getStallgroup();
            if (stall.isOrder_complete() || stall.isCancelled()){
                cancelOrder.setBackgroundResource(R.drawable.inactive_button);
                cancelOrder.setEnabled(false);
                if (stall.isCancelled()){
                    cancelOrder.setText("OTP : N.A." );
                }else {
                    cancelOrder.setText("OTP : " + transaction.getStallgroup().getUID());
                }

            }else if (stall.isOrder_ready()){
                cancelOrder.setVisibility(View.VISIBLE);
                cancelOrder.setEnabled(true);
            }else{
                Log.e("In on else", "Here");
                cancelOrder.setVisibility(View.VISIBLE);
                cancelOrder.setBackgroundResource(R.drawable.inactive_button);

                cancelOrder.setEnabled(false);
            }
        }
        else if (transactionType.equals("add")){
            nameText = "ADDED AMOUNT";
            cancelOrder.setVisibility(View.GONE);
        }else if (transactionType.equals("transfer")){
            nameText = "TRANSFERRED";
            cancelOrder.setVisibility(View.GONE);
        }else {
            nameText = "RECEIVED";
            cancelOrder.setVisibility(View.GONE);
        }

        name.setText(nameText);

        ImageView closeOverlay = findViewById(R.id.close_btn);
        cancelOrder.setTypeface(montReg);

        RecyclerView billList = findViewById(R.id.billed_item_list);
        List<BillItem> bill;
        bill = getBillItems(transaction);

        BilledItemsAdapter billedItemsAdapter = new BilledItemsAdapter(bill,activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        billList.setLayoutManager(layoutManager);
        billList.setAdapter(billedItemsAdapter);

        if(layout.getVisibility()== View.INVISIBLE){
            layout.startAnimation(slideUp);
            layout.setVisibility(View.VISIBLE);
        }
   

        clicked = false;
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                        findViewById(R.id.progress_uid).setVisibility(View.VISIBLE);
                    otpRequest(transaction.getStallgroup().getSgid(),cancelOrder,transaction);

                    clicked = true;
                }catch (Exception e){
                   e.printStackTrace();
                }

            }
        });

        closeOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked){
                    WalletLoadFragment loadFragment = new WalletLoadFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,loadFragment).commit();
                }
                if(layout.getVisibility()== View.VISIBLE){
                    layout.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            layout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });
    }

    void otpRequest(final String sgid, final Button cancelOrder, final Transaction transaction){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("sg_id",sgid);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.generate).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                                    cancelOrder.setText("OTP : " + transaction.getStallgroup().getUID());
                                    cancelOrder.setBackgroundResource(R.drawable.inactive_button);
                                    cancelOrder.setEnabled(false);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                                    Log.e("Error", anError.toString());
                                    Toast.makeText(activity,"Some error occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                        Toast.makeText(activity,"Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    List<BillItem> getBillItems(Transaction transaction){
        List<BillItem> list = new ArrayList<>();
        if (transaction.getT_type().equals("buy")){
            for (Sales sale: transaction.getStallgroup().getSales()){
                BillItem item= new BillItem();
                item.setItemName(sale.getProduct().getName() +" - " + sale.getProduct().getSize()
                        + " X " + sale.getQuantity());
                item.setCost("" + sale.getProduct().getPrice());
                list.add(item);
            }
        }else if (transaction.getT_type().equals("add")){
            BillItem item = new BillItem();
            item.setItemName("Ref - " + transaction.getId());
            item.setCost("Added INR " + transaction.getValue());
            list.add(item);
        }else if (transaction.getT_type().equals("swd")){
            BillItem item = new BillItem();
            item.setItemName("Ref - " + transaction.getId());
            item.setCost("Added INR " + transaction.getValue());
            list.add(item);
        }else {
            BillItem item = new BillItem();
            String from;
            Log.e("Item Click", transaction.getTransfer().toString());
            if (transaction.getTransfer().isIs_bitsian()){
                from = transaction.getTransfer().getBitsian().getName();
            }else {
                from = transaction.getTransfer().getParticipant().getName();
            }
            item.setItemName(from);
            item.setCost(String.valueOf(transaction.getValue()));
            list.add(item);
        }
        return list;
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

@Override
    public void onBackButtonFragment() {
       super.onBackPressed();
    }



    @Override
    public void transactionComplete() {
        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).commit();
    }

    public void transactionDone(){
        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).commit();
    }

    @Override
    public void closeBottomSheetListener() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
