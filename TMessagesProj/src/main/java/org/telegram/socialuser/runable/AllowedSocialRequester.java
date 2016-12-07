package org.telegram.socialuser.runable;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;

import java.util.ArrayList;

/**
 * Created by craterzone3 on 25/11/16.
 */

public class AllowedSocialRequester implements Runnable{


    ArrayList<CustomHttpParams> httpParamses =new ArrayList<>();

    public AllowedSocialRequester(String phone,String cc){
        httpParamses.add(new CustomHttpParams("mobile",phone));
        httpParamses.add(new CustomHttpParams("cc",cc));
    }

    @Override
    public void run() {

        try{
            CZResponse response = HttpUrlConnectionUtil.get(UriUtil.getAllowedRequestURL(),"application/json",httpParamses);
            if(response.getResponseCode()==200){

                JSONObject object= new JSONObject(response.getResponseString());
                int allowedRequest = object.getInt("noOfRequest");
                SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
                p.edit().putInt("allowedRequest",allowedRequest).commit();
            }
        }

        catch(Exception e){

            Log.d("Allowed Request"," Error in Allowed Social Request");

        }
    }



}
