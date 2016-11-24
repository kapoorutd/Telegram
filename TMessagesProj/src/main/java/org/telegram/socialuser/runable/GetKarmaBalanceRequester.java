package org.telegram.socialuser.runable;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.Logger;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.ui.listners.KarmaBalanceListener;

import java.util.ArrayList;

/**
 * Created by craterzone3 on 18/11/16.
 */

public class GetKarmaBalanceRequester implements Runnable {

    ArrayList<CustomHttpParams> params = new ArrayList<>();
    private String userPhoneNo;
    private String cCode;
    private KarmaBalanceListener listener;

    public GetKarmaBalanceRequester(String userPhoneNo ,String cCode, KarmaBalanceListener listener) {
            this.userPhoneNo = userPhoneNo;
            this.cCode=cCode;
            this.listener = listener;
    }

    @Override
    public void run() {

        try {
            params.add(new CustomHttpParams("mobile",userPhoneNo));
            params.add(new CustomHttpParams("cc",cCode));

            CZResponse data1 = HttpUrlConnectionUtil.get(UriUtil.getKarmaBalanceUrl(userPhoneNo), "application/json",params);
            if(data1!=null && data1.getResponseCode() == 200){
               JSONObject object= new JSONObject(data1.getResponseString());
                int noOfCredit = object.getInt("noOfCredit");
                SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
                p.edit().putString("karmaBal",String.valueOf(noOfCredit)).commit();
                listener.onGetKarmaSuccess(noOfCredit);
           }
           else
            {
                listener.onGetKarmaFailure();
            }
        }

        catch (Exception e) {
            Logger.d("GetKarmaBalanceRequester", e.getStackTrace().toString());
        }



    }
}