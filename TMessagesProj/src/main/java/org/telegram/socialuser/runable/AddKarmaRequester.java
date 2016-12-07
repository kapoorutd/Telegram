package org.telegram.socialuser.runable;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.payment.PaymentConfirmationListener;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CreditModel;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.ui.listners.OnKarmaAddedListener;

import java.util.ArrayList;

/**
 * Created by craterzone3 on 23/11/16.
 */

public class AddKarmaRequester implements Runnable {
    private CreditModel model;
    private ArrayList<CustomHttpParams> params =new ArrayList<>();
    OnKarmaAddedListener listener;
    public AddKarmaRequester(CreditModel model,OnKarmaAddedListener listener){
        this.model=model;
        this.listener=listener;
    }


    @Override
    public void run() {
        if (model != null) {
            try {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(model);
                CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getCreditUrl(), json, "application/json", "application/json", params);

                if(data1.getResponseCode()==200){
                    JSONObject object= new JSONObject(data1.getResponseString());
                    int noOfCredit = object.getInt("noOfCredit");
                    SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
                    p.edit().putString("karmaBal",String.valueOf(noOfCredit)).commit();
                    listener.onAddKarmaSuccess(noOfCredit);
                }
                else{
                    listener.onAddKarmaFailure();
                }
            }
            catch (Exception e) {
              }
              }
      }



}
