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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.listners.OnAddUserListner;

/**
 * Created by ram on 2/6/16.
 */
public class AddUserRequester implements Runnable {
    private TLRPC.TelegramUsers user;

    public AddUserRequester(TLRPC.TelegramUsers user, String url) {
        this.user = user;
    }

    @Override
    public void run() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(user);
       CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getHttpUrl("register"),json,null,"application/json", null);
        if(data1.getResponseCode() == 200){
            SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
            try {
                TLRPC.SocialUserResponse resUser = gson.fromJson(data1.getResponseString(), TLRPC.SocialUserResponse.class);
                p.edit().putBoolean("Login_Status",false).commit();
                p.edit().putString("sex",resUser.sex).commit();
                if(resUser.dob!=null) {
                    p.edit().putString("dob", Util.getDate(resUser.dob)).commit();
                }
                p.edit().putString("social_id",resUser.userId).commit();
                p.edit().putString("visibility",resUser.visibility).commit();
                p.edit().putString("userHash",resUser.userHash).commit();

                for(OnAddUserListner listner : ApplicationLoader.getInstance().getUIListeners(OnAddUserListner.class)){

                    listner.setUserAddSuccess();
                }


            }catch (Exception e){
            Logger.d("UserRegistration",e.getStackTrace().toString());
            }

        }
        else{

            for(OnAddUserListner listner : ApplicationLoader.getInstance().getUIListeners(OnAddUserListner.class)){

                listner.setUserAddFailed();
            }

        }



    }
}
