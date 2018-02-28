package com.example.sammy1997.bitswallet.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.crashlytics.android.Crashlytics;
import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.WalletActivity;
import com.example.sammy1997.bitswallet.utils.URLS;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    int RC_SIGN_IN = 12;
    ProgressBar progressBar;
    GoogleSignInClient googleSignInClient;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar6);
        preferences = getApplicationContext().getSharedPreferences("default",MODE_PRIVATE);
        editor = preferences.edit();
        SignInButton googleSignIn = findViewById(R.id.googleSignIn);
        googleSignIn.setOnClickListener(this);
        googleSignIn.setSize(SignInButton.SIZE_WIDE);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("975053221879-9dvv66a20imkc2f460pcepa41drds9nt.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.googleSignIn:
                progressBar.setVisibility(View.VISIBLE);
                if (GoogleSignIn.getLastSignedInAccount(this) != null)
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            signIn();
                        }
                    });
                else {
                    signIn();
            }
                break;
        }
    }

    void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progressBar.setVisibility(View.VISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            signedIn(account);
        }catch (ApiException e){
            Log.e("Error", "signInResult:failed code=" + e.getStatusCode());
            Log.e("Error", "sdjsjdkl" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            Toast.makeText(getApplicationContext(), "Error! Please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void signedIn(GoogleSignInAccount account){
        Log.e("token", account.getIdToken());

        JSONObject object = new JSONObject();
        try{
            object.put("id_token", account.getIdToken());
            AndroidNetworking.post(URLS.get_username).addJSONObjectBody(object)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (response.has("username") && response.has("password")){
                        Log.e("hio", "i ma here");
                        try {
                            editor.putString("username",response.getString("username"));
                            editor.putString("password",response.getString("password"));
                            editor.putBoolean("logged_in",true);
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,WalletActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error! Please try again", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Error! Please try again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Error! Please try again", Toast.LENGTH_LONG).show();
                }
            });
        }catch (JSONException e){

        }

    }
}
