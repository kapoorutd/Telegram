package org.telegram.socialuser.runable;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.Logger;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.Util;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.listners.OnSocialLogin;

import java.util.ArrayList;

/**
 * Created by ram on 13/6/16.
 */
public class GetUserRequester implements Runnable {

    private ArrayList<CustomHttpParams> params;
    private OnSocialLogin login;
    public GetUserRequester(ArrayList<CustomHttpParams> params, OnSocialLogin login){
        this.params = params;
        this.login = login;
    }


    @Override
    public void run() {
        CZResponse data1 = HttpUrlConnectionUtil.get(UriUtil.getHttpUrl("getUser"),"application/json",params);
        if(data1.getResponseCode() == 200){
            SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
            try {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                TLRPC.SocialUserResponse resUser = gson.fromJson(data1.getResponseString(), TLRPC.SocialUserResponse.class);
                if(resUser.id!=null) {
                    p.edit().putBoolean("Login_Status",false).commit();
                    p.edit().putString("social_id", resUser.userId).commit();
                    p.edit().putString("sex", resUser.sex).commit();
                    if(resUser.dob!=null){
                        p.edit().putString("dob", Util.getDate(resUser.dob)).commit();
                    }
                    p.edit().putString("visibility", resUser.visibility).commit();
                    p.edit().putString("userHash", resUser.userHash).commit();
                    login.onSocialFailer(resUser.userId);
                } else {
                  login.onSocialLoginSuccess();

                }
            }catch (Exception e){
                Logger.d("GetUserRequester",e.getStackTrace().toString());
            }
        }
       // login.onSocialLoginError();
    }
}
