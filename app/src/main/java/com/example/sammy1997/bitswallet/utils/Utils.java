package com.example.sammy1997.bitswallet.utils;

import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sammy on 9/2/18.
 */

public class Utils {

    public static String wallet_secret = "4b6e39873f40492aadee397b03316b62";
    public static String names[] = {"Satyansh Rai","Madhur Wadhwa","Vaibhav Maheshwari", "Sombuddha Chakravarty","Megh Thakkar","Tushar Goel","Aditya Laddha", "CCD Pandhare","Manav Kaushik"};
    public static String dept[] = {"Student's Union", "Dept. of Visual Media" ,"Dept. of Visual Media" ,"Dept. of Visual Media" ,"Dept. of Visual Media" ,"Dept. of Visual Media" ,"Dept. of Visual Media" ,"Dept. of Visual Media", "Student's Union"};
    public static String role[] = {"Ideation and Initiative", "UI Design", "Android App Developer","Android App Developer", "Backend Developer", "Backend Developer","Android App Developer", "Vendor Portal Developer","UI Design"};
    public static String phone[] = {"9151178228","7688883918","9529179518","9829996855","9829799877","9694345679","7987431519","7718823969","9463961799"};
    public static String email[] = {"f2016632@pilani.bits-pilani.ac.in","f2016727@pilani.bits-pilani.ac.in","f2016081@pilani.bits-pilani.ac.in","sombuddha2016@gmail.com","f2016036@pilani.bits-pilani.ac.in","f2016023@pilani.bits-pilani.ac.in","f2016038@pilani.bits-pilani.ac.in","f2016153@pilani.bits-pilani.ac.in","f2016472@pilani.bits-pilani.ac.in"};
    public static String imageUrl[] = {"developers/satyansh.jpg","developers/madhur.jpg","developers/vaibhav.jpg","developers/sammy.jpg","developers/gujju.jpg","developers/tushar.jpg","developers/laddha.jpg","developers/ccd.jpg","developers/manav.jpg"};
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static String username = "tushar004";
    public static String password = "qwertyuiop";

    public static JSONObject userObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject walletSecret(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("WALLET_TOKEN", wallet_secret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
